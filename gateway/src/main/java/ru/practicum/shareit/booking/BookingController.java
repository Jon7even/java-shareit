package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.NamesInController.API_BOOKINGS;
import static ru.practicum.shareit.constants.NamesInController.X_HEADER_USER_ID;

@Controller
@RequestMapping(path = API_BOOKINGS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(X_HEADER_USER_ID) long userId,
                                                @RequestBody @Valid BookingCreateTO bookingCreateTO) {
        log.info("Creating booking {}, userId={}", bookingCreateTO, userId);

        return bookingClient.createBooking(userId, bookingCreateTO);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(X_HEADER_USER_ID) @Positive long userId,
                                                 @PathVariable long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.findBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@RequestHeader(X_HEADER_USER_ID) @Positive long userId,
                                                 @PathVariable @Positive long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Approve booking {}, userId={}, status={}", bookingId, userId, approved);

        return bookingClient.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getListBookingByIdUser(
            @RequestHeader(X_HEADER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking by user with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        return bookingClient.getListBookingByIdUser(BookingRequestListTO.builder()
                .idUser(userId)
                .state(state)
                .from(from)
                .size(size)
                .build());
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemBookingByIdOwner(
            @RequestHeader(X_HEADER_USER_ID) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        return bookingClient.getAllItemBookingByIdOwner(BookingRequestListTO.builder()
                .idUser(userId)
                .state(state)
                .from(from)
                .size(size)
                .build());
    }

}
