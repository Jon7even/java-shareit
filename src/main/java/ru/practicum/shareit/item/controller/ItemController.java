package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemRequestUpdateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final ItemControllerMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDTO createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                      @Valid @RequestBody ItemRequestCreateDTO itemRequestCreateDTO,
                                      HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long getCheckedUserId = checkHeaderUserId(userId);

        Item itemCreate = itemService.createItem(
                mapper.toItemInServiceFromItemRequestCreateDTO(itemRequestCreateDTO, getCheckedUserId));

        return mapper.toItemResponseDTOFromItem(itemCreate);

    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDTO getItemById(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                       @PathVariable long itemId,
                                       HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long getCheckedUserId = checkHeaderUserId(userId);

        Item getItemById = itemService.findItemById(getCheckedUserId, itemId);

        return mapper.toItemResponseDTOFromItem(getItemById);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDTO> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                                     HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long getCheckedUserId = checkHeaderUserId(userId);

        List<Item> getAllItemsByUserId = itemService.getAllItemsByUserId(getCheckedUserId);

        return getAllItemsByUserId.stream().map(mapper::toItemResponseDTOFromItem).collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDTO updateUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                      @PathVariable long itemId,
                                      @Valid @RequestBody ItemRequestUpdateDTO itemRequestUpdateDTO,
                                      HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long getCheckedUserId = checkHeaderUserId(userId);

        Item itemUpdate = itemService.updateItem(
                mapper.toItemInServiceFromItemRequestUpdateDTO(itemRequestUpdateDTO, getCheckedUserId, itemId)
        );

        return mapper.toItemResponseDTOFromItem(itemUpdate);

    }

    private long checkHeaderUserId(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new IncorrectParameterException("X-Sharer-User-Id");
        } else {
            return userId.get();
        }
    }

}
