package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.NamesInController.API_ITEM_REQUESTS;
import static ru.practicum.shareit.constants.NamesInController.X_HEADER_USER_ID;

@Controller
@RequestMapping(path = API_ITEM_REQUESTS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(X_HEADER_USER_ID) @Positive long userId,
            @Valid @RequestBody ItemRequestCreateTO requestCreateTO) {
        log.info("Creating itemRequest {}, userId={}", requestCreateTO, userId);

        return requestClient.createItemRequest(userId, requestCreateTO);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(X_HEADER_USER_ID) @Positive long userId,
            @PathVariable @Positive long requestId) {
        log.info("Get itemRequest {}, userId={}", requestId, userId);

        return requestClient.findItemRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestByIdOwner(
            @RequestHeader(X_HEADER_USER_ID) @Positive long userId) {
        log.info("Get itemRequests by ownerId={}", userId);

        return requestClient.getAllItemRequestByIdOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getListItemRequestByAnyUser(
            @RequestHeader(X_HEADER_USER_ID) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {

        log.info("Get itemRequests, userId={}, from={}, size={}", userId, from, size);

        return requestClient.getListItemRequestByAnyUser(ItemRequestRequestListTO.builder()
                .idUser(userId)
                .from(from)
                .size(size)
                .build());
    }
}
