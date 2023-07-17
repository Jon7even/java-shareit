package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestCreateDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;

import java.util.Optional;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestCreateDTO bookingRequestCreateDTO, Optional<Long> idUser);

    BookingResponseDTO findBookingById(Optional<Long> idUser, Optional<Long> idBooking);

    BookingResponseDTO confirmBooking(Optional<Long> idUser, Optional<Long> idBooking, Optional<Boolean> approved);
}
