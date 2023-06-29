package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotCreatedException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemCreateInServiceDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

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
        Item itemCreateInRepository = createValidItem(itemCreateInServiceDTO);

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

    private Item createValidItem(ItemCreateInServiceDTO itemCreateInServiceDTO) {
        User getUser = getUserById(itemCreateInServiceDTO.getOwner());

        return Item.builder()
                .name(itemCreateInServiceDTO.getName())
                .description(itemCreateInServiceDTO.getDescription())
                .available(itemCreateInServiceDTO.isAvailable())
                .owner(getUser)
                .build();
    }

    private User getUserById(long idUser) {
        return userService.findUserById(idUser);
    }
}
