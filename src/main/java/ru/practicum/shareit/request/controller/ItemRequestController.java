package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestResponseTO> createItemRequest(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            @Valid @RequestBody ItemRequestCreateTO requestCreateTO,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                requestService.createItemRequest(requestCreateTO, userId)
        );
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseTO> getItemRequestById(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            @PathVariable Optional<Long> requestId,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(requestService.findItemRequestById(userId, requestId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseTO>> getAllItemRequestByIdOwner(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(requestService.getAllItemRequestByIdOwner(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseTO>> getListItemRequestByAnyUser(
            @RequestHeader(X_HEADER_USER_ID) Long userId,
            @RequestParam(required = false) Optional<Integer> from,
            @RequestParam(required = false) Optional<Integer> size,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(requestService.getListItemRequestByAnyUser(
                ItemRequestMapper.INSTANCE.toDTOFromRequestParam(userId, from, size))
        );
    }
}
