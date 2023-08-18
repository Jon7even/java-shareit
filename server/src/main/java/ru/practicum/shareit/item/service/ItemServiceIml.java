package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.CommonValidator;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_SORT_BY_ID;
import static ru.practicum.shareit.constants.NamesLogsInService.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {
    private final ItemRepository repositoryItem;
    private final UserRepository repositoryUser;
    private final BookingRepository repositoryBooking;
    private final ItemRequestRepository repositoryRequest;
    private final CommentRepository repositoryComment;

    @Transactional
    @Override
    public ItemResponseTO createItem(ItemCreateTO itemCreateTO, Optional<Long> idUser) {
        log.debug("New itemTO came {} [itemCreateTO={}]", SERVICE_FROM_CONTROLLER, itemCreateTO);
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);

        ItemEntity itemForCreateInRepository = validItemForCreate(itemCreateTO, checkedUserId);
        log.debug("Add new entity [item={}] {}", itemForCreateInRepository, SERVICE_IN_DB);

        ItemEntity createdItem = repositoryItem.save(itemForCreateInRepository);
        log.debug("New item has returned [item={}] {}", createdItem, SERVICE_FROM_DB);

        return ItemMapper.INSTANCE.toDTOResponseFromEntity(createdItem);
    }

    @Override
    public ItemResponseBookingAndCommentTO findItemById(Optional<Long> idUser, Optional<Long> idItem) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedItemId = CommonValidator.checkParameterItemId(idItem);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);

        log.debug("Get item by [idItem={}] by owner [user={}] {}", checkedItemId, checkedUserId, SERVICE_IN_DB);
        Optional<ItemEntity> foundItemById = repositoryItem.findById(checkedItemId);

        if (foundItemById.isPresent()) {
            log.debug("Found [item={}] {}", foundItemById.get(), SERVICE_FROM_DB);

            List<CommentResponseTO> listCommentsDTO = getListCommentsByItemFromDb(foundItemById.get());

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
                                     ItemUpdateTO itemUpdateTO) {
        log.debug("Item for update came {} [ItemRequestUpdateDTO={}]", SERVICE_FROM_CONTROLLER, itemUpdateTO);
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedItemId = CommonValidator.checkParameterItemId(idItem);

        ItemEntity itemUpdateInRepository = validItemForUpdate(itemUpdateTO, checkedUserId, checkedItemId);
        log.debug("Update entity [item={}] {}", itemUpdateTO, SERVICE_IN_DB);

        ItemEntity updatedItem = repositoryItem.save(itemUpdateInRepository);
        log.debug("Updated item has returned [item={}] {}", updatedItem, SERVICE_FROM_DB);

        return ItemMapper.INSTANCE.toDTOResponseFromEntity(updatedItem);
    }

    @Override
    public List<ItemResponseBookingAndCommentTO> getAllItemsByUserId(ItemRequestListTO itemRequestListTO) {
        Long validUserId = itemRequestListTO.getIdUser();
        existDoesUserEntityById(validUserId);

        Pageable page = CommonValidator.getPageRequest(
                itemRequestListTO.getFrom(), itemRequestListTO.getSize(), Optional.of(DEFAULT_SORT_BY_ID)
        );

        log.debug("Get all items {} by [idUser={}] [page={}]", SERVICE_IN_DB, validUserId, page);
        List<ItemEntity> itemsByIdUser = repositoryItem.findByUserId(validUserId, page);

        if (itemsByIdUser.isEmpty()) {
            log.debug("Has returned empty list items {} by [idUser={}]", SERVICE_FROM_DB, validUserId);
            return Collections.emptyList();
        } else {
            List<ItemResponseBookingAndCommentTO> listForResponseDTO = new ArrayList<>();

            for (ItemEntity item : itemsByIdUser) {
                List<CommentResponseTO> comments = getListCommentsByItemFromDb(item);
                listForResponseDTO.add(getItemWithBookingAndComment(item, comments));
            }

            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listForResponseDTO.size(), SERVICE_FROM_DB, validUserId);

            return listForResponseDTO;
        }
    }

    @Override
    public List<ItemShort> getListSearchItem(ItemRequestListTO itemRequestListTO) {
        Long validUserId = itemRequestListTO.getIdUser();
        String checkedSearchText = checkParameterSearchText(itemRequestListTO.getText());

        if (checkedSearchText.isBlank()) {
            log.debug("Has returned empty list items [searchText is empty] by [idUser={}]", validUserId);
            return Collections.emptyList();
        }

        existDoesUserEntityById(validUserId);
        Pageable page = CommonValidator.getPageRequest(
                itemRequestListTO.getFrom(), itemRequestListTO.getSize(), Optional.of(DEFAULT_SORT_BY_ID)
        );

        log.debug("Get list items [searchText={}] {} by [idUser={}] [page={}]",
                checkedSearchText, SERVICE_IN_DB, validUserId, page);
        List<ItemShort> listFoundItemsByText = repositoryItem.getListSearchItemShort(checkedSearchText, page);

        if (listFoundItemsByText.isEmpty()) {
            log.debug("Has returned empty list items [searchText={}] {} by [idUser={}]",
                    checkedSearchText, SERVICE_FROM_DB, validUserId);
        } else {
            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listFoundItemsByText.size(), SERVICE_FROM_DB, validUserId);
        }

        return listFoundItemsByText;
    }

    @Transactional
    @Override
    public void deleteItemById(Optional<Long> idUser, Optional<Long> idItem) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedItemId = CommonValidator.checkParameterItemId(idItem);

        existDoesUserEntityById(checkedUserId);
        existDoesItemEntityById(checkedItemId);
        checkIsUserTheOwnerOfItem(checkedItemId, checkedUserId);

        log.debug("Remove [item={}] by [userId={}] {}", checkedItemId, checkedUserId, SERVICE_IN_DB);
        repositoryItem.deleteById(checkedItemId);
        boolean isRemoved = repositoryItem.existsById(checkedItemId);

        if (!isRemoved) {
            log.debug("Item by [idItem={}] [idOwner{}] has removed {}",
                    checkedItemId, checkedUserId, SERVICE_FROM_DB);
        } else {
            log.error("Item by [id={}] [idOwner{}] was not removed", checkedItemId, checkedUserId);
            throw new EntityNotDeletedException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Transactional
    @Override
    public CommentResponseTO createComment(Optional<Long> idUser, Optional<Long> idItem,
                                           CommentCreateTO comment) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedItemId = CommonValidator.checkParameterItemId(idItem);

        CommentEntity commentForCreateInRepository = validCommentForCreate(checkedUserId, checkedItemId, comment);

        log.debug("Add new [comment={}] {}", commentForCreateInRepository, SERVICE_IN_DB);

        CommentEntity createdComment = repositoryComment.save(commentForCreateInRepository);
        log.debug("New comment has returned [comment={}] {}", createdComment, SERVICE_FROM_DB);

        return CommentMapper.INSTANCE.toDTOResponseFromEntity(createdComment);
    }

    private CommentEntity validCommentForCreate(Long checkedUserId, Long checkedItemId, CommentCreateTO comment) {
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        ItemEntity checkedItemFromDB = findItemEntityById(checkedItemId);
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
                            checkedUserId, checkedItemId)
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

    private ItemEntity validItemForCreate(ItemCreateTO itemCreateTO, Long checkedUserId) {
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        Optional<Long> checkedRequestId = checkParameterRequestId(itemCreateTO);

        if (checkedRequestId.isPresent()) {
            ItemRequestEntity checkedRequestFromDB = findItemRequestEntityById(checkedRequestId.get());
            log.debug("[itemName={}] creates for [itemRequestId={}]", itemCreateTO.getName(), checkedRequestId);
            return ItemMapper.INSTANCE.toEntityFromDTOCreateWithRequest(
                    itemCreateTO, checkedUserFromDB, checkedRequestFromDB
            );
        } else {
            log.debug("[itemName={}] creates without [itemRequest]", itemCreateTO.getName());
            return ItemMapper.INSTANCE.toEntityFromDTOCreate(itemCreateTO, checkedUserFromDB);
        }
    }

    private ItemEntity validItemForUpdate(ItemUpdateTO itemRequestUpdateDTO,
                                          Long checkedUserId,
                                          Long checkedItemId) {

        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        ItemEntity checkedItemFromDB = findItemEntityById(checkedItemId);
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
        if (!idOwnerOfItem.equals(idUserRequest)) {
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
            log.warn("User by [idUser={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private void existDoesUserEntityById(Long checkedUserId) {
        log.debug("Checking entity user by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        boolean existsById = repositoryUser.existsById(checkedUserId);

        if (existsById) {
            log.debug("Check was successful [idUser={}] exist in {}", checkedUserId, SERVICE_IN_DB);
        } else {
            log.warn("User by [idUser={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private ItemRequestEntity findItemRequestEntityById(Long checkedRequestId) {
        log.debug("Get ItemRequest entity for checking by [idItemRequest={}] {}", checkedRequestId, SERVICE_IN_DB);
        Optional<ItemRequestEntity> foundCheckRequest = repositoryRequest.findById(checkedRequestId);

        if (foundCheckRequest.isPresent()) {
            log.debug("Check was successful found [ItemRequest={}] {}", foundCheckRequest.get(), SERVICE_FROM_DB);
            return foundCheckRequest.get();
        } else {
            log.warn("ItemRequest by [idItemRequest={}] was not found", checkedRequestId);
            throw new EntityNotFoundException(String.format("ItemRequest with [idItemRequest=%d]", checkedRequestId));
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

    private void existDoesItemEntityById(Long checkedItemId) {
        log.debug("Checking entity item by [idItem={}] {}", checkedItemId, SERVICE_IN_DB);
        boolean existsById = repositoryItem.existsById(checkedItemId);

        if (existsById) {
            log.debug("Check was successful [idItem={}] exist in {}", checkedItemId, SERVICE_IN_DB);
        } else {
            log.warn("Item by [idItem={}] was not found", checkedItemId);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", checkedItemId));
        }
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
            throw new IncorrectParameterException("searchText");
        }

        return searchText.get();
    }

    private Optional<Long> checkParameterRequestId(ItemCreateTO itemRequestCreateDTO) {
        Long requestId = itemRequestCreateDTO.getRequestId();
        if (!(requestId == null)) {
            if (requestId > 0) {
                log.debug("Checking [requestId={}] for [itemName={}] is ok", requestId, itemRequestCreateDTO.getName());
                return Optional.of(requestId);
            }
        }

        return Optional.empty();
    }

}
