package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.NamesInController.API_ITEMS;
import static ru.practicum.shareit.constants.NamesInController.X_HEADER_USER_ID;

@Controller
@RequestMapping(path = API_ITEMS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(X_HEADER_USER_ID) @Positive Long userId,
                                             @Valid @RequestBody ItemCreateTO itemRequestCreateDTO) {
        log.info("Creating item {}, userId={}", itemRequestCreateDTO, userId);

        return itemClient.createItem(userId, itemRequestCreateDTO);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(X_HEADER_USER_ID) @Positive Long userId,
            @PathVariable @Positive Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);

        return itemClient.findItemById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestHeader(X_HEADER_USER_ID) @Positive Long userId,
                                                 @PathVariable @Positive Long itemId,
                                                 @Valid @RequestBody ItemUpdateTO itemRequestUpdateDTO) {
        log.info("Update Item {}, userId={}, itemId={}", itemRequestUpdateDTO, userId, itemId);

        return itemClient.updateItem(userId, itemId, itemRequestUpdateDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(
            @RequestHeader(X_HEADER_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Get item list by user userId={}, from={}, size={}", userId, from, size);

        return itemClient.getAllItemsByUserId(ItemRequestListTO.builder()
                .idUser(userId)
                .from(from)
                .size(size).build());
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemBySearchBar(
            @RequestHeader(X_HEADER_USER_ID) Long userId,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Get item list by userId={}, from={}, size={}, textSearch={}", userId, from, size, text);

        return itemClient.getListSearchItem(ItemRequestListTO.builder()
                .idUser(userId)
                .text(text)
                .from(from)
                .size(size).build());
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> removeItemById(@RequestHeader(X_HEADER_USER_ID) @Positive Long userId,
                                                 @PathVariable @Positive Long itemId) {
        log.info("Remove itemId={} by userId={}", itemId, userId);

        return itemClient.deleteItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(X_HEADER_USER_ID) @Positive Long userId,
                                                @PathVariable @Positive Long itemId,
                                                @Valid @RequestBody CommentCreateTO commentCreateTO) {
        log.info("Create comment {} by userId={} on itemId={}", commentCreateTO, userId, itemId);

        return itemClient.createComment(userId, itemId, commentCreateTO);
    }

}
