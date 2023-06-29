package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

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
        if (userId.isPresent() && userId.get() > 0) {
            Item itemCreate = itemService.createItem(
                    mapper.toItemInServiceFromItemRequestDTO(itemRequestCreateDTO, userId.get())
            );

            return mapper.toItemResponseDTOFromItem(itemCreate);
        } else {
            throw new IncorrectParameterException("X-Sharer-User-Id");
        }

    }
}
