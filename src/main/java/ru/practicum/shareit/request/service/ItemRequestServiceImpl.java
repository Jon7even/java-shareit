package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.CommonValidator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_COUNT_SIZE;
import static ru.practicum.shareit.constants.NamesLogsInService.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repositoryRequest;
    private final UserRepository repositoryUser;
    private final ItemRepository repositoryItem;

    @Transactional
    @Override
    public ItemRequestResponseTO createItemRequest(ItemRequestCreateTO itemRequestCreateTO, Optional<Long> idUser) {
        log.debug("New ItemRequestTO came {} [ItemRequestCreateTO={}]", SERVICE_FROM_CONTROLLER, itemRequestCreateTO);
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);

        ItemRequestEntity requestForCreateInRepository = validItemRequestForCreate(itemRequestCreateTO, checkedUserId);
        log.debug("Add new entity [itemRequest={}] {}", requestForCreateInRepository, SERVICE_IN_DB);

        ItemRequestEntity createdItemRequest = repositoryRequest.save(requestForCreateInRepository);
        log.debug("New ItemRequest has returned [itemRequest={}] {}", createdItemRequest, SERVICE_FROM_DB);

        return ItemRequestMapper.INSTANCE.toDTOResponseFromEntity(createdItemRequest, Collections.emptyList());
    }

    @Override
    public ItemRequestResponseTO findItemRequestById(Optional<Long> idUser, Optional<Long> requestId) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedRequestId = CommonValidator.checkParameterItemRequestId(requestId);
        existDoesUserEntityById(checkedUserId);

        log.debug("Get itemRequest by [idItemRequest={}] by [idUser={}] {}",
                checkedRequestId, checkedUserId, SERVICE_IN_DB);
        Optional<ItemRequestEntity> foundItemRequestById = repositoryRequest.findById(checkedRequestId);

        if (foundItemRequestById.isPresent()) {
            log.debug("Found [itemRequest={}] {}", foundItemRequestById.get(), SERVICE_FROM_DB);

            List<ItemResponseTO> listItemsDTO = getListItemsByItemRequestFromDb(foundItemRequestById.get());

            return ItemRequestMapper.INSTANCE.toDTOResponseFromEntity(foundItemRequestById.get(), listItemsDTO);
        } else {
            log.warn("Item by [idItem={}] was not found", checkedRequestId);
            throw new EntityNotFoundException(String.format("ItemRequest with [idItemRequest=%d]", checkedRequestId));
        }
    }

    @Override
    public List<ItemRequestResponseTO> getAllItemRequestByIdOwner(Optional<Long> idUser) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);

        log.debug("Get list itemRequests {} by owner [idUser={}]", SERVICE_IN_DB, checkedUserId);
        List<ItemRequestEntity> listRequestsByOwner = repositoryRequest.findAllItemsRequestsByOwner(checkedUserFromDB);

        if (listRequestsByOwner.isEmpty()) {
            log.debug("Has returned empty itemRequests {} by [idUser={}]", SERVICE_FROM_DB, checkedUserId);
            return Collections.emptyList();
        } else {
            List<ItemRequestResponseTO> listItemRequestTO = new ArrayList<>();

            for (ItemRequestEntity itemRequest : listRequestsByOwner) {
                List<ItemResponseTO> listItemsDTO = getListItemsByItemRequestFromDb(itemRequest);
                listItemRequestTO.add(ItemRequestMapper.INSTANCE.toDTOResponseFromEntity(itemRequest, listItemsDTO));
            }

            log.debug("Found list itemRequests [count={}] {} by [idUser={}]",
                    listItemRequestTO.size(), SERVICE_FROM_DB, checkedUserId);

            return listItemRequestTO;
        }
    }

    @Override
    public List<ItemRequestResponseTO> getListItemRequestByAnyUser(ItemRequestRequestListTO itemRequestRequestListTO) {
        Long checkedUserId = CommonValidator.checkParameterUserId(itemRequestRequestListTO.getIdUser());

        Optional<Sort> sort = Optional.of(Sort.by(Sort.Direction.DESC, "created"));
        Pageable page = CommonValidator.getPageRequest(
                itemRequestRequestListTO.getFrom(), itemRequestRequestListTO.getSize(), sort
        );

        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);

        log.debug("Get list itemRequests {} by [idUser={}] [page={}]", SERVICE_IN_DB, checkedUserId, page);
        List<ItemRequestEntity> requestListByPages = repositoryRequest.findAll(page).stream()
                .filter(itemRequestEntity -> !itemRequestEntity.getRequestor().equals(checkedUserFromDB))
                .collect(Collectors.toList());

        if (requestListByPages.isEmpty()) {
            log.debug("Has returned empty itemRequests {} by [idUser={}]", SERVICE_FROM_DB, checkedUserId);
            return Collections.emptyList();
        } else {
            List<ItemRequestResponseTO> listItemRequestTO = new ArrayList<>();

            for (ItemRequestEntity itemRequest : requestListByPages) {
                List<ItemResponseTO> listItemsDTO = getListItemsByItemRequestFromDb(itemRequest);
                listItemRequestTO.add(ItemRequestMapper.INSTANCE.toDTOResponseFromEntity(itemRequest, listItemsDTO));
            }

            log.debug("Found list itemRequests [count={}] {} by [idUser={}]",
                    listItemRequestTO.size(), SERVICE_FROM_DB, checkedUserId);

            return listItemRequestTO;
        }
    }

    private List<ItemResponseTO> getListItemsByItemRequestFromDb(ItemRequestEntity itemRequestEntity) {
        List<ItemEntity> listItemsByItemRequest = repositoryItem.findAllItemsByRequest(itemRequestEntity);
        log.debug("Found [countListItems={}] {}", listItemsByItemRequest.size(), SERVICE_FROM_DB);

        return listItemsByItemRequest.stream()
                .map(ItemMapper.INSTANCE::toDTOResponseFromEntity)
                .collect(Collectors.toList());
    }

    private ItemRequestEntity validItemRequestForCreate(ItemRequestCreateTO itemRequestCreateTO, Long checkedUserId) {
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        LocalDateTime created = LocalDateTime.now();

        return ItemRequestMapper.INSTANCE.toEntityFromDTOCreate(itemRequestCreateTO, checkedUserFromDB, created);
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

}
