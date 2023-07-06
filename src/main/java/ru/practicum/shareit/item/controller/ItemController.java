package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemRequestUpdateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.HttpServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemRequestCreateDTO, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> getItemById(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                       @PathVariable Optional<Long> itemId,
                                                       HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(itemService.findItemById(userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> updateItemById(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                          @PathVariable Optional<Long> itemId,
                                                          @Valid @RequestBody ItemRequestUpdateDTO itemRequestUpdateDTO,
                                                          HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemRequestUpdateDTO));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItemsByUserId(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        List<ItemResponseDTO> getAllItemsByUserId = itemService.getAllItemsByUserId(userId);

        return ResponseEntity.ok()
                .header(X_COUNT_ITEMS, String.valueOf(getAllItemsByUserId.size()))
                .body(getAllItemsByUserId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDTO>> searchItemBySearchBar(
            @RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
            @RequestParam Optional<String> text,
            HttpServletRequest request) {

        log.debug("On {} {} {}", HttpServletUtils.getURLWithParam(request), IN_CONTROLLER_METHOD, request.getMethod());

        List<ItemResponseDTO> listFoundItemsBySearchBar = itemService.getListSearchItem(userId, text);

        return ResponseEntity.ok()
                .header(X_COUNT_ITEMS, String.valueOf(listFoundItemsBySearchBar.size()))
                .body(listFoundItemsBySearchBar);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItemById(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                               @PathVariable Optional<Long> itemId,
                                               HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        itemService.deleteItemById(userId, itemId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
