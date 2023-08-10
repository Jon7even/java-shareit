package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingResponseTO createBooking(BookingCreateTO bookingRequestCreateDTO, Optional<Long> idUser);

    BookingResponseTO findBookingById(Optional<Long> idUser, Optional<Long> idBooking);

    BookingResponseTO confirmBooking(Optional<Long> idUser, Optional<Long> idBooking, Optional<Boolean> approved);

    List<BookingResponseTO> getListBookingByIdUser(BookingRequestListTO bookingRequestListTO);

    List<BookingResponseTO> getAllItemBookingByIdOwner(BookingRequestListTO bookingRequestListTO);
}
