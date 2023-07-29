package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingQueueTO;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;

import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.projections.ItemShort;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_COUNT_SIZE;
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
    public ItemResponseTO createItem(ItemCreateTO itemRequestCreateDTO, Optional<Long> idUser) {
        log.debug("New item came {} [ItemRequestCreateDTO={}]", SERVICE_FROM_CONTROLLER, itemRequestCreateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        ItemEntity itemForCreateInRepository = validItemForCreate(itemRequestCreateDTO, checkedUserId);

        log.debug("Add new [item={}] {}", itemForCreateInRepository, SERVICE_IN_DB);
        ItemEntity createdItem = repositoryItem.save(itemForCreateInRepository);
        Optional<ItemEntity> foundItemAfterCreation = repositoryItem.findById(createdItem.getId());

        if (foundItemAfterCreation.isPresent() && createdItem.equals(foundItemAfterCreation.get())) {
            log.debug("New item has returned [item={}] {}", createdItem, SERVICE_FROM_DB);
            return ItemMapper.INSTANCE.toDTOResponseFromEntity(createdItem);
        } else {
            log.error("[item={}] was not created", createdItem);
            throw new EntityNotCreatedException("New item");
        }
    }

    @Override
    public ItemResponseBookingAndCommentTO findItemById(Optional<Long> idUser, Optional<Long> idItem) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);

        log.debug("Get item by [id={}] owner [user={}] {}", checkedItemId, checkedUserId, SERVICE_IN_DB);
        Optional<ItemEntity> foundItemById = repositoryItem.findById(checkedItemId);

        if (foundItemById.isPresent()) {
            log.debug("Found [item={}] {}", foundItemById.get(), SERVICE_FROM_DB);

            List<CommentResponseTO> listCommentsDTO = getListCommentsByItemFromDb(foundItemById.get());
            log.debug("Found [countListComments={}] {}", listCommentsDTO.size(), SERVICE_FROM_DB);

            if (foundItemById.get().getUser().equals(checkedUserFromDB)) {
                ItemResponseBookingAndCommentTO itemResponseDTO = getItemWithBookingAndComment(
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
    public ItemResponseTO updateItem(Optional<Long> idUser,
                                     Optional<Long> idItem,
                                     ItemUpdateTO itemRequestUpdateDTO) {
        log.debug("Item for update came {} [ItemRequestUpdateDTO={}]", SERVICE_FROM_CONTROLLER, itemRequestUpdateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);

        ItemEntity itemUpdateInRepository = validItemForUpdate(itemRequestUpdateDTO, checkedUserId, checkedItemId);

        log.debug("Update [item={}] {}", itemRequestUpdateDTO, SERVICE_IN_DB);
        ItemEntity updatedItem = repositoryItem.save(itemUpdateInRepository);
        Optional<ItemEntity> foundItemAfterUpdate = repositoryItem.findById(checkedItemId);

        if (foundItemAfterUpdate.isPresent() && updatedItem.equals(foundItemAfterUpdate.get())) {
            log.debug("Updated item has returned [item={}] {}", updatedItem, SERVICE_FROM_DB);
            return ItemMapper.INSTANCE.toDTOResponseFromEntity(updatedItem);
        } else {
            log.error("[item={}] was not updated", itemUpdateInRepository);
            throw new EntityNotUpdatedException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Override
    public List<ItemResponseBookingAndCommentTO> getAllItemsByUserId(ItemRequestListTO itemRequestListTO) {
        Long checkedUserId = checkParameterUserId(itemRequestListTO.getIdUser());
        findUserEntityById(checkedUserId);
        Pageable page = getPageRequest(itemRequestListTO);

        log.debug("Get all items {} by [idUser={}] [page={}]", SERVICE_IN_DB, checkedUserId, page);
        List<ItemEntity> itemsByIdUser = repositoryItem.findByUserId(checkedUserId, page);

        if (itemsByIdUser.isEmpty()) {
            log.debug("Has returned empty list items {} by [idUser={}]", SERVICE_FROM_DB, checkedUserId);
            return Collections.emptyList();
        } else {
            List<ItemResponseBookingAndCommentTO> listForResponseDTO = new ArrayList<>();

            for (ItemEntity item : itemsByIdUser) {
                List<CommentResponseTO> comments = getListCommentsByItemFromDb(item);
                listForResponseDTO.add(getItemWithBookingAndComment(item, comments));
            }

            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listForResponseDTO.size(), SERVICE_FROM_DB, checkedUserId);

            return listForResponseDTO;
        }
    }

    @Override
    public List<ItemShort> getListSearchItem(ItemRequestListTO itemRequestListTO) {
        Long checkedUserId = checkParameterUserId(itemRequestListTO.getIdUser());
        String checkedSearchText = checkParameterSearchText(itemRequestListTO.getText());

        if (checkedSearchText.isBlank()) {
            log.debug("Has returned empty list items [searchText is empty] by [idUser={}]", checkedUserId);
            return Collections.emptyList();
        }

        findUserEntityById(checkedUserId);
        Pageable page = getPageRequest(itemRequestListTO);

        log.debug("Get list items [searchText={}] {} by [idUser={}] [page={}]",
                checkedSearchText, SERVICE_IN_DB, checkedUserId, page);
        List<ItemShort> listFoundItemsByText = repositoryItem.getListSearchItemShort(checkedSearchText, page);

        if (listFoundItemsByText.isEmpty()) {
            log.debug("Has returned empty list items [searchText={}] {} by [idUser={}]",
                    checkedSearchText, SERVICE_FROM_DB, checkedUserId);
        } else {
            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listFoundItemsByText.size(), SERVICE_FROM_DB, checkedUserId);
        }

        return listFoundItemsByText;
    }

    @Transactional
    @Override
    public void deleteItemById(Optional<Long> idUser, Optional<Long> idItem) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);

        ItemEntity checkedItemFromDB = findItemEntityById(checkedItemId);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
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
    public CommentResponseTO createComment(Optional<Long> idUser, Optional<Long> idItem,
                                           CommentCreateTO comment) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedItemId = checkParameterItemId(idItem);

        CommentEntity commentForCreateInRepository = validCommentForCreate(checkedUserId, checkedItemId, comment);

        log.debug("Add new [comment={}] {}", commentForCreateInRepository, SERVICE_IN_DB);

        CommentEntity createdComment = repositoryComment.save(commentForCreateInRepository);
        Optional<CommentEntity> foundCommentAfterCreation = repositoryComment.findById(createdComment.getId());

        if (foundCommentAfterCreation.isPresent() && createdComment.equals(foundCommentAfterCreation.get())) {
            log.debug("New comment has returned [comment={}] {}", createdComment, SERVICE_FROM_DB);
            return CommentMapper.INSTANCE.toDTOResponseFromEntity(createdComment);
        } else {
            log.error("[comment={}] was not created", createdComment);
            throw new EntityNotCreatedException("New comment");
        }
    }

    private PageRequest getPageRequest(ItemRequestListTO itemRequestListTO) {
        boolean isExistParamOfSize = itemRequestListTO.getFrom().isPresent();
        boolean isExistParamOfPage = itemRequestListTO.getSize().isPresent();
        int page = 0;
        int size = DEFAULT_COUNT_SIZE;

        if (isExistParamOfSize && isExistParamOfPage) {
            int pageFromDTO = itemRequestListTO.getFrom().get();
            int sizeFromDTO = itemRequestListTO.getSize().get();

            if (pageFromDTO >= 0 && sizeFromDTO >= 1) {
                page = pageFromDTO / sizeFromDTO;
                size = sizeFromDTO;
            } else {
                log.warn("User used incorrect parameters: [from] and [size] [bookingRequestListTO={}]",
                        itemRequestListTO);
                throw new IncorrectParameterException("from and size");
            }
        }

        return PageRequest.of(page, size);
    }

    private CommentEntity validCommentForCreate(Long userId, Long itemId, CommentCreateTO comment) {
        UserEntity checkedUserFromDB = findUserEntityById(userId);
        ItemEntity checkedItemFromDB = findItemEntityById(itemId);
        LocalDateTime currentTime = LocalDateTime.now();

        List<BookingEntity> pastBookingItem = repositoryBooking.getBookingByOwnerBeforeCurrentTime(checkedUserFromDB,
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

    private List<CommentResponseTO> getListCommentsByItemFromDb(ItemEntity item) {
        List<CommentEntity> listCommentsByItem = repositoryComment.findAllCommentsByItem(item);
        log.debug("Found [countListComments={}] {}", listCommentsByItem.size(), SERVICE_FROM_DB);

        return listCommentsByItem.stream()
                .map(CommentMapper.INSTANCE::toDTOResponseFromEntity)
                .collect(Collectors.toList());
    }

    private ItemResponseBookingAndCommentTO getItemWithBookingAndComment(ItemEntity item,
                                                                         List<CommentResponseTO> listCommentsDTO) {
        List<BookingEntity> listBookingByItem = repositoryBooking.findByItemOrderByStart(item);
        LocalDateTime currentTime = LocalDateTime.now();

        BookingQueueTO lastBooking = listBookingByItem.stream()
                .sorted(Comparator.comparing(BookingEntity::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(currentTime)
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper.INSTANCE::toDTOResponseShortFromEntity)
                .findFirst().orElse(null);

        BookingQueueTO nextBooking = listBookingByItem.stream()
                .filter(booking -> booking.getStart().isAfter(currentTime)
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper.INSTANCE::toDTOResponseShortFromEntity)
                .findFirst().orElse(null);

        log.debug("Found [lastBooking={}] and [nextBooking={}] {}", lastBooking, nextBooking, SERVICE_FROM_DB);

        return ItemMapper.INSTANCE.toDTOResponseWithCommentsByOwnerFromEntity(
                item, lastBooking, nextBooking, listCommentsDTO
        );
    }

    private ItemEntity validItemForCreate(ItemCreateTO itemRequestCreateDTO, Long checkedUserId) {
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);

        return ItemMapper.INSTANCE.toEntityFromDTOCreate(itemRequestCreateDTO, checkedUserFromDB);
    }

    private ItemEntity validItemForUpdate(ItemUpdateTO itemRequestUpdateDTO,
                                          Long checkedUserId,
                                          Long checkedItemId) {

        ItemEntity checkedItemFromDB = findItemEntityById(checkedItemId);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        checkIsUserTheOwnerOfItem(checkedItemFromDB.getUser().getId(), checkedUserFromDB.getId());

        ItemEntity buildValidItem = ItemEntity.builder()
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

    private void checkIsUserTheOwnerOfItem(Long idOwnerOfItem, Long idUserRequest) {
        if (idOwnerOfItem != idUserRequest) {
            log.error("User [idUserRequest={}] was attempt to apply unauthorized method " +
                    "to Owner [idOwnerOfItem={}]", idUserRequest, idOwnerOfItem);
            throw new AccessDeniedException("this item");
        }
    }

    private UserEntity findUserEntityById(Long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<UserEntity> foundCheckUser = repositoryUser.findById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private ItemEntity findItemEntityById(Long checkedItemId) {
        log.debug("Get item entity for checking by [idItem={}] {}", checkedItemId, SERVICE_IN_DB);
        Optional<ItemEntity> foundCheckItem = repositoryItem.findById(checkedItemId);

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
