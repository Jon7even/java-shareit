package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_STATE_IN_CONTROLLER;
import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseTO> createBooking(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                           @Valid @RequestBody BookingCreateTO bookingCreate,
                                                           HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookingCreate, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseTO> getBookingById(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                            @PathVariable Optional<Long> bookingId,
                                                            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(bookingService.findBookingById(userId, bookingId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseTO> confirmBooking(@RequestHeader(X_HEADER_USER_ID) Optional<Long> userId,
                                                            @PathVariable Optional<Long> bookingId,
                                                            @RequestParam Optional<Boolean> approved,
                                                            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(bookingService.confirmBooking(userId, bookingId, approved));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseTO>> getListBookingByIdUser(
            @RequestHeader(X_HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = DEFAULT_STATE_IN_CONTROLLER) BookingState state,
            @RequestParam(required = false) Optional<Integer> from,
            @RequestParam(required = false) Optional<Integer> size,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(bookingService.getListBookingByIdUser(
                BookingMapper.INSTANCE.toDTOFromRequestParam(userId, state, from, size))
        );
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseTO>> getAllItemBookingByIdOwner(
            @RequestHeader(X_HEADER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = DEFAULT_STATE_IN_CONTROLLER) BookingState state,
            @RequestParam(required = false) Optional<Integer> from,
            @RequestParam(required = false) Optional<Integer> size,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(bookingService.getAllItemBookingByIdOwner(
                BookingMapper.INSTANCE.toDTOFromRequestParam(userId, state, from, size))
        );
    }

}
