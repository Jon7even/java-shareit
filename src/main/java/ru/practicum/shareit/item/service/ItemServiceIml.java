package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.MapperItemDTO;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_FROM_DB;
import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_IN_DB;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {
    private final ItemDao repositoryItem;
    private final UserDao repositoryUser;

    @Override
    public ItemResponseDTO createItem(ItemRequestCreateDTO itemRequestCreateDTO, Optional<Long> idUser) {
        long checkedUserId = checkParameterUserId(idUser);
        Item itemCreateInRepository = validItemForCreate(itemRequestCreateDTO, checkedUserId);

        log.debug("Add new [item={}] {}", itemRequestCreateDTO, SERVICE_IN_DB);
        Optional<Item> createdItem = repositoryItem.createItem(itemCreateInRepository);

        if (createdItem.isPresent()) {
            log.debug("New item has returned [item={}] {}", createdItem.get(), SERVICE_FROM_DB);
            return MapperItemDTO.toItemResponseDTOFromItem(createdItem.get());
        } else {
            log.error("[item={}] was not created", createdItem);
            throw new EntityNotCreatedException("New item");
        }
    }

    @Override
    public ItemResponseDTO findItemById(Optional<Long> idUser, Optional<Long> idItem) {
        long checkedUserId = checkParameterUserId(idUser);
        long checkedItemId = checkParameterItemId(idItem);
        findUserEntityById(checkedUserId);

        log.debug("Get item by [id={}] owner [user={}] {}", checkedItemId, checkedUserId, SERVICE_IN_DB);
        Optional<Item> foundItemById = repositoryItem.findItemById(checkedItemId);

        if (foundItemById.isPresent()) {
            log.debug("Found [item={}] {}", foundItemById.get(), SERVICE_FROM_DB);
            return MapperItemDTO.toItemResponseDTOFromItem(foundItemById.get());
        } else {
            log.warn("Item by [idItem={}] owner [idUser={}] was not found", checkedItemId, checkedUserId);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Override
    public ItemResponseDTO updateItem(Optional<Long> idUser,
                                      Optional<Long> idItem,
                                      ItemRequestUpdateDTO itemRequestUpdateDTO) {
        long checkedUserId = checkParameterUserId(idUser);
        long checkedItemId = checkParameterItemId(idItem);

        Item itemUpdateInRepository = validItemForUpdate(itemRequestUpdateDTO, checkedUserId, checkedItemId);

        log.debug("Update [item={}] {}", itemRequestUpdateDTO, SERVICE_IN_DB);
        Optional<Item> updatedItem = repositoryItem.updateItem(itemUpdateInRepository);

        if (updatedItem.isPresent()) {
            log.debug("Updated item has returned [item={}] {}", updatedItem.get(), SERVICE_FROM_DB);
            return MapperItemDTO.toItemResponseDTOFromItem(updatedItem.get());
        } else {
            log.error("[item={}] was not updated", itemUpdateInRepository);
            throw new EntityNotUpdatedException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    @Override
    public List<ItemResponseDTO> getAllItemsByUserId(Optional<Long> idUser) {
        long checkedUserId = checkParameterUserId(idUser);
        findUserEntityById(checkedUserId);

        log.debug("Get all items {} by [idUser={}]", SERVICE_IN_DB, checkedUserId);
        List<Item> listItemsByIdUser = repositoryItem.getAllItemsByUserId(checkedUserId);

        if (listItemsByIdUser.isEmpty()) {
            log.debug("Has returned empty list items {} by [idUser={}]", SERVICE_FROM_DB, idUser);
        } else {
            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listItemsByIdUser.size(), SERVICE_FROM_DB, idUser);
        }

        return listItemsByIdUser.stream().map(MapperItemDTO::toItemResponseDTOFromItem).collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDTO> getListSearchItem(Optional<Long> idUser,
                                                   Optional<String> searchText) {
        long checkedUserId = checkParameterUserId(idUser);
        String checkedSearchText = checkParameterSearchText(searchText);

        if (checkedSearchText.isBlank()) {
            log.debug("Has returned empty list items [searchText is empty] by [idUser={}]", checkedUserId);
            return Collections.emptyList();
        }

        findUserEntityById(checkedUserId);

        log.debug("Get list items [searchText={}] {} by [idUser={}]", checkedSearchText, SERVICE_IN_DB, idUser);
        List<Item> listFoundItemsByText = repositoryItem.getListSearchItem(checkedSearchText);

        if (listFoundItemsByText.isEmpty()) {
            log.debug("Has returned empty list items [searchText={}] {} by [idUser={}]",
                    checkedSearchText, SERVICE_FROM_DB, idUser);
        } else {
            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listFoundItemsByText.size(), SERVICE_FROM_DB, idUser);
        }

        return listFoundItemsByText.stream().map(MapperItemDTO::toItemResponseDTOFromItem).collect(Collectors.toList());
    }

    private Item validItemForCreate(ItemRequestCreateDTO itemRequestCreateDTO, long checkedUserId) {
        User checkedUserFromDB = findUserEntityById(checkedUserId);

        return MapperItemDTO.toItemFromItemRequestCreateDTO(itemRequestCreateDTO, checkedUserFromDB);
    }

    private Item validItemForUpdate(ItemRequestUpdateDTO itemRequestUpdateDTO,
                                    long checkedUserId,
                                    long checkedItemId) {

        Item checkedItemFromDB = findItemEntityById(checkedItemId);
        User checkedUserFromDB = findUserEntityById(checkedUserId);
        checkIsUserTheOwnerOfItem(checkedItemFromDB.getOwner().getId(), checkedUserFromDB.getId());

        Item buildValidItem = Item.builder()
                .id(checkedItemId)
                .owner(checkedUserFromDB)
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

    private User findUserEntityById(long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<User> foundCheckUser = repositoryUser.findUserById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private Item findItemEntityById(long checkedItemId) {
        log.debug("Get item entity for checking by [idItem={}] {}", checkedItemId, SERVICE_IN_DB);
        Optional<Item> foundCheckItem = repositoryItem.findItemById(checkedItemId);

        if (foundCheckItem.isPresent()) {
            log.debug("Check was successful found [item={}] {}", foundCheckItem.get(), SERVICE_FROM_DB);
            return foundCheckItem.get();
        } else {
            log.warn("Item by [idItem={}] was not found", checkedItemId);
            throw new EntityNotFoundException(String.format("Item with [idUser=%d]", checkedItemId));
        }
    }

    private long checkParameterUserId(Optional<Long> idUser) {
        if (idUser.isPresent()) {
            if (idUser.get() > 0) {
                log.debug("Checking Header[param={}] [idUser={}] is ok", X_HEADER_USER_ID, idUser.get());
            }
        } else {
            throw new IncorrectParameterException(X_HEADER_USER_ID);
        }

        return idUser.get();
    }

    private long checkParameterItemId(Optional<Long> idItem) {
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
