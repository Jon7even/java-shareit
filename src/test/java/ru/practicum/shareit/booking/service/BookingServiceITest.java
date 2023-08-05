package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.setup.GenericServiceTest;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

public class BookingServiceITest extends GenericServiceTest {
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBooking() {
        initTestVariable(true, true, false);
        userEntity.setId(2L);
        itemEntity.setUser(userEntity);
        bookingEntity.setStatus(WAITING);
        when(bookingRepository.save(any()))
                .thenReturn(bookingEntity);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));

        BookingCreateTO originalDto = BookingCreateTO.builder()
                .itemId(bookingEntity.getItem().getId())
                .start(bookingEntity.getStart())
                .end(bookingEntity.getEnd())
                .build();

        initOptionalVariable();
        BookingResponseTO result = bookingService.createBooking(originalDto, idUserOptional);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getStart(), equalTo(originalDto.getStart()));
        assertThat(result.getEnd(), equalTo(originalDto.getEnd()));
        assertThat(result.getItem().getId(), equalTo(originalDto.getItemId()));
        assertThat(result.getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.getStatus(), equalTo(bookingEntity.getStatus()));
        verify(bookingRepository, times(1)).save(any(BookingEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

}
