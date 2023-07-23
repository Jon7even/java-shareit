package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingQueueDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;

import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.projections.ItemShort;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.NamesLogsInService.*;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {
    private final ItemRepository repositoryItem;
    private final UserRepository repositoryUser;
    private final BookingRepository repositoryBooking;
    private final CommentRepository repositoryComment;

    @Transactional
    @Override
    public ItemResponseDTO createItem(ItemRequestCreateDTO itemRequestCreateDTO, Optional<Long> idUser) {
        log.debug("New item came {} [ItemRequestCreateDTO={}]", SERVICE_FROM_CONTROLLER, itemRequestCreateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        Item itemForCreateInRepository = validItemForCreate(itemRequestCreateDTO, checkedUserId);

        log.debug("Add new [item={}] {}", itemForCreateInRepository, SERVICE_IN_DB);
        Item createdItem = repositoryItem.save(itemForCreateInRepository);
        Optional<Item> foundItemAfterCreation = repositoryItem.findById(createdItem.getId());

        if (foundItemAfterCreation.isPresent() && createdItem.equals(foundItemAfterCreation.get())) {
            log.debug("New item has returned [item={}] {}", createdItem, SERVICE_FROM_DB);
            return ItemMapper.INSTANCE.toDTOResponseFromEntity(createdItem);
        } else {
            log.error("[item={}] was not created", createdItem);
            throw new EntityNotCreatedException("New item");
        }
    }

    @Override
    public ItemResponseBookingAndCommentDTO findItemById(Optional<Long> idUser, Optional<Long> idItem) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);
        User checkedUserFromDB = findUserEntityById(checkedUserId);

        log.debug("Get item by [id={}] owner [user={}] {}", checkedItemId, checkedUserId, SERVICE_IN_DB);
        Optional<Item> foundItemById = repositoryItem.findById(checkedItemId);

        if (foundItemById.isPresent()) {
            log.debug("Found [item={}] {}", foundItemById.get(), SERVICE_FROM_DB);

            List<CommentResponseDTO> listCommentsDTO = getListCommentsByItemFromDb(foundItemById.get());
            log.debug("Found [countListComments={}] {}", listCommentsDTO.size(), SERVICE_FROM_DB);

            if (foundItemById.get().getUser().equals(checkedUserFromDB)) {
                ItemResponseBookingAndCommentDTO itemResponseDTO = getItemWithBookingAndComment(
                        foundItemById.get(), listCommentsDTO);
                log.debug("Return item with booking queue");

                return itemResponseDTO;
            } else {
                log.debug("Return item without booking queue");
                return ItemMapper.INSTANCE.toDTOResponseWithCommentsFromEntity(foundItemById.get(), listCommentsDTO);
            }

        } else {
            log.warn("Item by [idItem={}] owner [idUser={}] was not found", checkedItemId, checkedUserId);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Transactional
    @Override
    public ItemResponseDTO updateItem(Optional<Long> idUser,
                                      Optional<Long> idItem,
                                      ItemRequestUpdateDTO itemRequestUpdateDTO) {
        log.debug("Item for update came {} [ItemRequestUpdateDTO={}]", SERVICE_FROM_CONTROLLER, itemRequestUpdateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);

        Item itemUpdateInRepository = validItemForUpdate(itemRequestUpdateDTO, checkedUserId, checkedItemId);

        log.debug("Update [item={}] {}", itemRequestUpdateDTO, SERVICE_IN_DB);
        Item updatedItem = repositoryItem.save(itemUpdateInRepository);
        Optional<Item> foundItemAfterUpdate = repositoryItem.findById(checkedItemId);

        if (foundItemAfterUpdate.isPresent() && updatedItem.equals(foundItemAfterUpdate.get())) {
            log.debug("Updated item has returned [item={}] {}", updatedItem, SERVICE_FROM_DB);
            return ItemMapper.INSTANCE.toDTOResponseFromEntity(updatedItem);
        } else {
            log.error("[item={}] was not updated", itemUpdateInRepository);
            throw new EntityNotUpdatedException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Override
    public List<ItemResponseBookingAndCommentDTO> getAllItemsByUserId(Optional<Long> idUser) {
        Long checkedUserId = checkParameterUserId(idUser);
        findUserEntityById(checkedUserId);

        log.debug("Get all items {} by [idUser={}]", SERVICE_IN_DB, checkedUserId);
        List<Item> itemsByIdUser = repositoryItem.findByUserId(checkedUserId);

        if (itemsByIdUser.isEmpty()) {
            log.debug("Has returned empty list items {} by [idUser={}]", SERVICE_FROM_DB, idUser);
            return Collections.emptyList();
        } else {
            List<ItemResponseBookingAndCommentDTO> listForResponseDTO = new ArrayList<>();

            for (Item item : itemsByIdUser) {
                List<CommentResponseDTO> comments = getListCommentsByItemFromDb(item);
                listForResponseDTO.add(getItemWithBookingAndComment(item, comments));
            }

            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listForResponseDTO.size(), SERVICE_FROM_DB, idUser);

            return listForResponseDTO;
        }
    }

    @Override
    public List<ItemShort> getListSearchItem(Optional<Long> idUser,
                                             Optional<String> searchText) {
        long checkedUserId = checkParameterUserId(idUser);
        String checkedSearchText = checkParameterSearchText(searchText);

        if (checkedSearchText.isBlank()) {
            log.debug("Has returned empty list items [searchText is empty] by [idUser={}]", checkedUserId);
            return Collections.emptyList();
        }

        findUserEntityById(checkedUserId);

        log.debug("Get list items [searchText={}] {} by [idUser={}]", checkedSearchText, SERVICE_IN_DB, idUser);
        List<ItemShort> listFoundItemsByText = repositoryItem.getListSearchItemShort(checkedSearchText);

        if (listFoundItemsByText.isEmpty()) {
            log.debug("Has returned empty list items [searchText={}] {} by [idUser={}]",
                    checkedSearchText, SERVICE_FROM_DB, idUser);
        } else {
            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listFoundItemsByText.size(), SERVICE_FROM_DB, idUser);
        }

        return listFoundItemsByText;
    }

    @Transactional
    @Override
    public void deleteItemById(Optional<Long> idUser, Optional<Long> idItem) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);

        Item checkedItemFromDB = findItemEntityById(checkedItemId);
        User checkedUserFromDB = findUserEntityById(checkedUserId);
        checkIsUserTheOwnerOfItem(checkedItemFromDB.getUser().getId(), checkedUserFromDB.getId());

        log.debug("Remove [item={}] by [userId={}] {}", checkedItemFromDB, checkedUserFromDB.getId(), SERVICE_IN_DB);
        repositoryItem.deleteById(checkedItemId);
        boolean isRemoved = repositoryItem.existsById(checkedItemId);

        if (!isRemoved) {
            log.debug("Item by [id={}] [ownerName{}] has removed {}",
                    checkedItemId, checkedUserFromDB.getName(), SERVICE_FROM_DB);
        } else {
            log.error("Item by [id={}] [ownerName{}] was not removed", checkedItemId, checkedUserFromDB.getName());
            throw new EntityNotDeletedException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Transactional
    @Override
    public CommentResponseDTO createComment(Optional<Long> idUser, Optional<Long> idItem,
                                            CommentRequestCreateDTO comment) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);

        Comment commentForCreateInRepository = validCommentForCreate(checkedUserId, checkedItemId, comment);

        log.debug("Add new [comment={}] {}", commentForCreateInRepository, SERVICE_IN_DB);

        Comment createdComment = repositoryComment.save(commentForCreateInRepository);
        Optional<Comment> foundCommentAfterCreation = repositoryComment.findById(createdComment.getId());

        if (foundCommentAfterCreation.isPresent() && createdComment.equals(foundCommentAfterCreation.get())) {
            log.debug("New comment has returned [comment={}] {}", createdComment, SERVICE_FROM_DB);
            return CommentMapper.INSTANCE.toDTOResponseFromEntity(createdComment);
        } else {
            log.error("[comment={}] was not created", createdComment);
            throw new EntityNotCreatedException("New comment");
        }
    }

    private Comment validCommentForCreate(Long userId, Long itemId, CommentRequestCreateDTO comment) {
        User checkedUserFromDB = findUserEntityById(userId);
        Item checkedItemFromDB = findItemEntityById(itemId);
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> pastBookingItem = repositoryBooking.getBookingByOwnerBeforeCurrentTime(checkedUserFromDB,
                checkedItemFromDB, currentTime);

        if (pastBookingItem.size() > 0) {
            return CommentMapper.INSTANCE.toEntityFromDTOCreate(
                    comment, checkedItemFromDB, checkedUserFromDB, currentTime
            );
        } else {
            throw new NoCompletedBookingsException(
                    String.format("User with [idUser=%d] not have completed bookings for this item[idItem=%d]",
                            userId, itemId)
            );
        }
    }

    private List<CommentResponseDTO> getListCommentsByItemFromDb(Item item) {
        List<Comment> listCommentsByItem = repositoryComment.findAllCommentsByItem(item);
        log.debug("Found [countListComments={}] {}", listCommentsByItem.size(), SERVICE_FROM_DB);

        return listCommentsByItem.stream()
                .map(CommentMapper.INSTANCE::toDTOResponseFromEntity)
                .collect(Collectors.toList());
    }

    private ItemResponseBookingAndCommentDTO getItemWithBookingAndComment(Item item,
                                                                          List<CommentResponseDTO> listCommentsDTO) {
        List<Booking> listBookingByItem = repositoryBooking.findByItemOrderByStart(item);
        LocalDateTime currentTime = LocalDateTime.now();

        BookingQueueDTO lastBooking = listBookingByItem.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(currentTime)
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper.INSTANCE::toDTOResponseShortFromEntity)
                .findFirst().orElse(null);

        BookingQueueDTO nextBooking = listBookingByItem.stream()
                .filter(booking -> booking.getStart().isAfter(currentTime)
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper.INSTANCE::toDTOResponseShortFromEntity)
                .findFirst().orElse(null);

        log.debug("Found [lastBooking={}] and [nextBooking={}] {}", lastBooking, nextBooking, SERVICE_FROM_DB);

        return ItemMapper.INSTANCE.toDTOResponseWithCommentsByOwnerFromEntity(
                item, lastBooking, nextBooking, listCommentsDTO
        );
    }

    private Item validItemForCreate(ItemRequestCreateDTO itemRequestCreateDTO, Long checkedUserId) {
        User checkedUserFromDB = findUserEntityById(checkedUserId);

        return ItemMapper.INSTANCE.toEntityFromDTOCreate(itemRequestCreateDTO, checkedUserFromDB);
    }

    private Item validItemForUpdate(ItemRequestUpdateDTO itemRequestUpdateDTO,
                                    Long checkedUserId,
                                    Long checkedItemId) {

        Item checkedItemFromDB = findItemEntityById(checkedItemId);
        User checkedUserFromDB = findUserEntityById(checkedUserId);
        checkIsUserTheOwnerOfItem(checkedItemFromDB.getUser().getId(), checkedUserFromDB.getId());

        Item buildValidItem = Item.builder()
                .id(checkedItemId)
                .user(checkedUserFromDB)
                .build();

        if (itemRequestUpdateDTO.getName() == null) {
            buildValidItem.setName(checkedItemFromDB.getName());
        } else {
            buildValidItem.setName(itemRequestUpdateDTO.getName());
        }

        if (itemRequestUpdateDTO.getDescription() == null) {
            buildValidItem.setDescription(checkedItemFromDB.getDescription());
        } else {
            buildValidItem.setDescription(itemRequestUpdateDTO.getDescription());
        }

        if (itemRequestUpdateDTO.getAvailable() == null) {
            buildValidItem.setAvailable(checkedItemFromDB.isAvailable());
        } else {
            buildValidItem.setAvailable(itemRequestUpdateDTO.getAvailable());
        }

        return buildValidItem;
    }

    private void checkIsUserTheOwnerOfItem(long idOwnerOfItem, long idUserRequest) {
        if (idOwnerOfItem != idUserRequest) {
            log.error("User [idUserRequest={}] was attempt to apply unauthorized method " +
                    "to Owner [idOwnerOfItem={}]", idUserRequest, idOwnerOfItem);
            throw new AccessDeniedException("this item");
        }
    }

    private User findUserEntityById(Long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<User> foundCheckUser = repositoryUser.findById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private Item findItemEntityById(Long checkedItemId) {
        log.debug("Get item entity for checking by [idItem={}] {}", checkedItemId, SERVICE_IN_DB);
        Optional<Item> foundCheckItem = repositoryItem.findById(checkedItemId);

        if (foundCheckItem.isPresent()) {
            log.debug("Check was successful found [item={}] {}", foundCheckItem.get(), SERVICE_FROM_DB);
            return foundCheckItem.get();
        } else {
            log.warn("Item by [idItem={}] was not found", checkedItemId);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    private Long checkParameterUserId(Optional<Long> idUser) {
        if (idUser.isPresent()) {
            if (idUser.get() > 0) {
                log.debug("Checking Header[param={}] [idUser={}] is ok", X_HEADER_USER_ID, idUser.get());
            }
        } else {
            throw new IncorrectParameterException(X_HEADER_USER_ID);
        }

        return idUser.get();
    }

    private Long checkParameterItemId(Optional<Long> idItem) {
        if (idItem.isPresent()) {
            if (idItem.get() > 0) {
                log.debug("Checking Path [idItem={}] is ok", idItem.get());
            }
        } else {
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", idItem.get()));
        }

        return idItem.get();
    }

    private String checkParameterSearchText(Optional<String> searchText) {
        if (searchText.isPresent()) {
            if (!searchText.get().isBlank()) {
                log.debug("Checking Path [searchText={}] is ok", searchText);
            } else {
                log.warn("Checking Path [searchText] is empty");
                return "";
            }
        } else {
            throw new IncorrectParameterException(String.format("searchText [text=%d]", searchText.get()));
        }

        return searchText.get();
    }

}
