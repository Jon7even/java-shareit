package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemRequestUpdateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.MapperItemDTO;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.HttpServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.NamesParametersInController.X_COUNT_ITEMS;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;
import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                      @Valid @RequestBody ItemRequestCreateDTO itemRequestCreateDTO,
                                                      HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long checkedUserId = checkHeaderUserId(userId);

        Item itemCreate = itemService.createItem(
                MapperItemDTO.toItemInServiceFromItemRequestCreateDTO(itemRequestCreateDTO, checkedUserId));

        return ResponseEntity.status(HttpStatus.CREATED).body(MapperItemDTO.toItemResponseDTOFromItem(itemCreate));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> getItemById(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                       @PathVariable long itemId,
                                                       HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long checkedUserId = checkHeaderUserId(userId);

        Item getItemById = itemService.findItemById(checkedUserId, itemId);

        return ResponseEntity.ok().body(MapperItemDTO.toItemResponseDTOFromItem(getItemById));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItemsByUserId(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long checkedUserId = checkHeaderUserId(userId);

        List<Item> getAllItemsByUserId = itemService.getAllItemsByUserId(checkedUserId);

        return ResponseEntity.ok()
                .header(X_COUNT_ITEMS, String.valueOf(getAllItemsByUserId.size()))
                .body(getAllItemsByUserId.stream().map(MapperItemDTO::toItemResponseDTOFromItem)
                        .collect(Collectors.toList()));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> updateItemById(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                          @PathVariable long itemId,
                                                          @Valid @RequestBody ItemRequestUpdateDTO itemRequestUpdateDTO,
                                                          HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        long checkedUserId = checkHeaderUserId(userId);

        Item itemUpdate = itemService.updateItem(
                MapperItemDTO.toItemInServiceFromItemRequestUpdateDTO(itemRequestUpdateDTO, checkedUserId, itemId)
        );

        return ResponseEntity.ok().body(MapperItemDTO.toItemResponseDTOFromItem(itemUpdate));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDTO>> searchItemBySearchBar(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            @RequestParam Optional<String> text,
            HttpServletRequest request) {

        log.debug("On {} {} {}", HttpServletUtils.getURLWithParam(request), IN_CONTROLLER_METHOD, request.getMethod());
        long checkedUserId = checkHeaderUserId(userId);

        if (text.isPresent() && !text.get().isBlank()) {
            List<Item> listFoundItemsByText = itemService.getListSearchItem(checkedUserId, text.get());

            return ResponseEntity.ok()
                    .header(X_COUNT_ITEMS, String.valueOf(listFoundItemsByText.size()))
                    .body(listFoundItemsByText.stream()
                            .map(MapperItemDTO::toItemResponseDTOFromItem)
                            .collect(Collectors.toList()));
        } else {
            return ResponseEntity.ok()
                    .header(X_COUNT_ITEMS, String.valueOf(0)).body(Collections.emptyList());
        }

    }

    private long checkHeaderUserId(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new IncorrectParameterException(X_HEADER_USER_ID);
        } else {
            return userId.get();
        }
    }

}
