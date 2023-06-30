package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotCreatedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityNotUpdatedException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemCreateInServiceDTO;
import ru.practicum.shareit.item.dto.ItemUpdateInRepositoryDTO;
import ru.practicum.shareit.item.dto.ItemUpdateInServiceDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_FROM_DB;
import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_IN_DB;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {
    private final ItemDao repositoryItem;
    private final UserService userService;

    @Override
    public Item createItem(ItemCreateInServiceDTO itemCreateInServiceDTO) {
        Item itemCreateInRepository = validItemForCreate(itemCreateInServiceDTO);

        log.debug("Add new [item={}] {}", itemCreateInServiceDTO, SERVICE_IN_DB);
        Optional<Item> createdItem = repositoryItem.createItem(itemCreateInRepository);

        if (createdItem.isPresent()) {
            log.debug("New item has returned [item={}] {}", createdItem.get(), SERVICE_FROM_DB);
            return createdItem.get();
        } else {
            log.error("[item={}] was not created", createdItem);
            throw new EntityNotCreatedException("New item");
        }
    }

    @Override
    public Item findItemById(long idUser, long idItem) {
        getUserById(idUser);

        log.debug("Get item by [id={}] owner [idUser={}] {}", idItem, idUser, SERVICE_IN_DB);
        Optional<Item> foundItemById = repositoryItem.findItemById(idItem);

        if (foundItemById.isPresent()) {
            log.debug("Found [item={}] {}", foundItemById.get(), SERVICE_FROM_DB);
            return foundItemById.get();
        } else {
            log.warn("Item by [idItem={}] owner [idUser={}] was not found", idItem, idUser);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", idItem));
        }
    }

    @Override
    public List<Item> getAllItemsByUserId(long idUser) {
        log.debug("Get all items {} by [idUser={}]", SERVICE_IN_DB, idUser);
        List<Item> listItemsByIdUser = repositoryItem.getAllItemsByUserId(idUser);

        if (listItemsByIdUser.isEmpty()) {
            log.debug("Has returned empty list items {} by [idUser={}]", SERVICE_FROM_DB, idUser);
        } else {
            log.debug("Found list items [count={}] {} by [idUser={}]",
                    listItemsByIdUser.size(), SERVICE_FROM_DB, idUser);
        }

        return listItemsByIdUser;
    }

    @Override
    public Item updateItem(ItemUpdateInServiceDTO itemUpdateInServiceDTO) {
        ItemUpdateInRepositoryDTO itemUpdateInRepository = validItemForUpdate(itemUpdateInServiceDTO);

        log.debug("Update [item={}] {}", itemUpdateInRepository, SERVICE_IN_DB);
        Optional<Item> updatedItem = repositoryItem.updateItem(itemUpdateInRepository);

        if (updatedItem.isPresent()) {
            log.debug("Updated item has returned [item={}] {}", updatedItem.get(), SERVICE_FROM_DB);
            return updatedItem.get();
        } else {
            log.error("[item={}] was not updated", itemUpdateInServiceDTO);
            throw new EntityNotUpdatedException(String.format("Item with [idItem=%d]", itemUpdateInServiceDTO.getId()));
        }
    }

    private Item validItemForCreate(ItemCreateInServiceDTO itemCreateInServiceDTO) {
        User getUserById = getUserById(itemCreateInServiceDTO.getOwner());

        return Item.builder()
                .name(itemCreateInServiceDTO.getName())
                .description(itemCreateInServiceDTO.getDescription())
                .available(itemCreateInServiceDTO.isAvailable())
                .owner(getUserById)
                .build();
    }

    private ItemUpdateInRepositoryDTO validItemForUpdate(ItemUpdateInServiceDTO itemUpdateInServiceDTO) {
        Item checkedItem = findItemById(itemUpdateInServiceDTO.getOwner(), itemUpdateInServiceDTO.getId());
        checkIsUserTheOwnerOfItem(itemUpdateInServiceDTO.getOwner(), checkedItem.getOwner().getId());

        ItemUpdateInRepositoryDTO buildValidItem = ItemUpdateInRepositoryDTO.builder()
                .id(itemUpdateInServiceDTO.getId())
                .owner(checkedItem.getOwner())
                .build();

        if (itemUpdateInServiceDTO.getName() == null) {
            buildValidItem.setName(checkedItem.getName());
        } else {
            buildValidItem.setName(itemUpdateInServiceDTO.getName());
        }

        if (itemUpdateInServiceDTO.getDescription() == null) {
            buildValidItem.setDescription(checkedItem.getDescription());
        } else {
            buildValidItem.setDescription(itemUpdateInServiceDTO.getDescription());
        }

        if (itemUpdateInServiceDTO.getAvailable() == null) {
            buildValidItem.setAvailable(checkedItem.isAvailable());
        } else {
            buildValidItem.setAvailable(itemUpdateInServiceDTO.getAvailable());
        }

        return buildValidItem;
    }

    private void checkIsUserTheOwnerOfItem(long idOwnerFromController, long idOwnerFromRepository) {
        if (idOwnerFromController != idOwnerFromRepository) {
            log.error("User [idOwnerFromController={}] was attempt to apply unauthorized method " +
                            "to Owner [idOwnerFromRepository={}]",idOwnerFromController, idOwnerFromRepository);
            throw new AccessDeniedException("this item");
        }
    }

    private User getUserById(long idUser) {
        return userService.findUserById(idUser);
    }
}
