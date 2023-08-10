package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityAlreadyBookedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.setup.GenericServiceTest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

public class BookingServiceITest extends GenericServiceTest {
    private BookingService bookingService;

    private BookingRequestListTO bookingRequestListTO;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    private void initBookingRequestList() {
        initOptionalVariable();
        bookingRequestListTO = BookingRequestListTO.builder()
                .idUser(id)
                .state(state)
                .from(fromOptional)
                .size(sizeOptional)
                .build();
    }

    @Test
    void createBooking_whenIdUserNull() {
        initOptionalVariable();
        initTestVariable(true, true, false);
        BookingCreateTO originalDto = BookingCreateTO.builder()
                .itemId(bookingEntity.getItem().getId())
                .start(bookingEntity.getStart())
                .end(bookingEntity.getEnd())
                .build();

        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(
                originalDto, Optional.empty()));

        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void createBooking_whenStartTimeEqualEndTime() {
        initOptionalVariable();
        initTestVariable(true, true, false);
        BookingCreateTO originalDto = BookingCreateTO.builder()
                .itemId(bookingEntity.getItem().getId())
                .start(bookingEntity.getStart().withNano(0))
                .end(bookingEntity.getStart().withNano(0))
                .build();

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.createBooking(
                originalDto, idUserOptional));

        String errorMessage = "[Field [start and end time] invalid: [Time booking [" + originalDto.getStart() +
                "] and [" + originalDto.getEnd() + "] is equals]]";

        assertThat(ex.getMessage(), equalTo(errorMessage));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void createBooking_whenEndTimeBeforeStartTime() {
        initOptionalVariable();
        initTestVariable(true, true, false);
        BookingCreateTO originalDto = BookingCreateTO.builder()
                .itemId(bookingEntity.getItem().getId())
                .start(bookingEntity.getEnd().withNano(0))
                .end(bookingEntity.getStart().withNano(0))
                .build();

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.createBooking(
                originalDto, idUserOptional));

        String errorMessage = "[Field [endTime] invalid: [Time booking [" + originalDto.getEnd() +
                "] more [" + originalDto.getStart() + "]]]";

        assertThat(ex.getMessage(), equalTo(errorMessage));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void createBooking_valid() {
        initTestVariable(true, true, false);
        userEntity.setId(2L);
        itemEntity.setUser(userEntity);
        bookingEntity.setStatus(BookingStatus.WAITING);
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

    @Test
    void createBooking_whenItemNotAvailable() {
        initTestVariable(true, true, false);
        userEntity.setId(2L);
        itemEntity.setUser(userEntity);
        itemEntity.setAvailable(false);
        bookingEntity.setStatus(BookingStatus.WAITING);
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
        assertThrows(EntityAlreadyBookedException.class, () -> bookingService.createBooking(
                originalDto, idUserOptional
        ));

        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void findBookingById_whenIdBookingNull() {
        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> bookingService.findBookingById(
                idUserOptional, Optional.empty()
        ));

        verify(userRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void findBookingById_whenIdUserNull() {
        initOptionalVariable();
        assertThrows(IncorrectParameterException.class, () -> bookingService.findBookingById(
                Optional.empty(), idBookingOptional
        ));

        verify(userRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void findBookingById_whenUserNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> bookingService.findBookingById(
                idUserOptional, idBookingOptional
        ));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void findBookingById_whenUserExistButBookingNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> bookingService.findBookingById(
                idUserOptional, idBookingOptional
        ));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void findBookingById_whenUserNotOwnerItemAndNotOwnerBooking() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        userEntity.setId(2L);
        itemEntity.setUser(userEntity);
        bookingEntity.setUser(userEntity);
        bookingEntity.setItem(itemEntity);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> bookingService.findBookingById(
                idUserOptional, idBookingOptional
        ));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void findBookingById_whenUserOwnerItem() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        userEntity.setId(2L);
        itemEntity.setUser(userEntity);
        bookingEntity.setItem(itemEntity);
        userEntity.setId(1L);
        bookingEntity.setUser(userEntity);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        initOptionalVariable();
        BookingResponseTO result = bookingService.findBookingById(
                idUserOptional, idBookingOptional
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingEntity.getId()));
        assertThat(result.getStart(), notNullValue());
        assertThat(result.getEnd(), notNullValue());
        assertThat(result.getItem(), notNullValue());
        assertThat(result.getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.getBooker(), notNullValue());
        assertThat(result.getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void findBookingById_whenUserOwnerBooking() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        userEntity.setId(2L);
        bookingEntity.setUser(userEntity);

        userEntity.setId(1L);
        itemEntity.setUser(userEntity);
        bookingEntity.setItem(itemEntity);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        initOptionalVariable();
        BookingResponseTO result = bookingService.findBookingById(
                idUserOptional, idBookingOptional
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingEntity.getId()));
        assertThat(result.getStart(), notNullValue());
        assertThat(result.getEnd(), notNullValue());
        assertThat(result.getItem(), notNullValue());
        assertThat(result.getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.getBooker(), notNullValue());
        assertThat(result.getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void confirmBooking_whenApprovedNull() {
        initOptionalVariable();
        IncorrectParameterException ex = assertThrows(
                IncorrectParameterException.class, () -> bookingService.confirmBooking(
                        idUserOptional, idBookingOptional, Optional.empty()
                ));

        assertThat(ex.getMessage(), equalTo("Parameter [approved] incorrect"));
        verify(userRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmBooking_whenIdUserNull() {
        initOptionalVariable();
        IncorrectParameterException ex = assertThrows(
                IncorrectParameterException.class, () -> bookingService.confirmBooking(
                        Optional.empty(), idBookingOptional, Optional.of(true)
                ));

        assertThat(ex.getMessage(), equalTo("Parameter [X-Sharer-User-Id] incorrect"));
        verify(userRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmBooking_whenIdBookingNull() {
        initOptionalVariable();
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookingService.confirmBooking(
                idUserOptional, Optional.empty(), Optional.of(true)
        ));

        assertThat(ex.getMessage(), equalTo("[Booking] not found"));
        verify(userRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmBooking_whenUserNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        initOptionalVariable();
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookingService.confirmBooking(
                idUserOptional, idBookingOptional, Optional.of(true)
        ));
        String errorMessage = "[User with [idUser=" + idUserOptional.get() + "]] not found";

        assertThat(ex.getMessage(), equalTo(errorMessage));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmBooking_whenUserExistButBookingNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookingService.confirmBooking(
                idUserOptional, idBookingOptional, Optional.of(true)
        ));
        String errorMessage = "[Booking with [idBooking=" + idBookingOptional.get() + "]] not found";

        assertThat(ex.getMessage(), equalTo(errorMessage));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmBooking_whenApprovedIsTrue() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        bookingEntity.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        initTestVariable(true, true, false);
        bookingEntity.setStatus(APPROVED);
        when(bookingRepository.save(any()))
                .thenReturn(bookingEntity);

        initOptionalVariable();
        BookingResponseTO result = bookingService.confirmBooking(
                idUserOptional, idBookingOptional, Optional.of(true)
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingEntity.getId()));
        assertThat(result.getStart(), notNullValue());
        assertThat(result.getEnd(), notNullValue());
        assertThat(result.getItem(), notNullValue());
        assertThat(result.getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.getBooker(), notNullValue());
        assertThat(result.getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.getStatus(), equalTo(APPROVED));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void confirmBooking_whenApprovedIsFalse() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        bookingEntity.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        initTestVariable(true, true, false);
        bookingEntity.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.save(any()))
                .thenReturn(bookingEntity);

        initOptionalVariable();
        BookingResponseTO result = bookingService.confirmBooking(
                idUserOptional, idBookingOptional, Optional.of(false)
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingEntity.getId()));
        assertThat(result.getStart(), notNullValue());
        assertThat(result.getEnd(), notNullValue());
        assertThat(result.getItem(), notNullValue());
        assertThat(result.getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.getBooker(), notNullValue());
        assertThat(result.getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.getStatus(), equalTo(BookingStatus.REJECTED));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void confirmBooking_whenApprovedIsAlreadyTrue() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        initOptionalVariable();
        assertThrows(IncorrectParameterException.class, () -> bookingService.confirmBooking(
                idUserOptional, idBookingOptional, Optional.of(true)
        ));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenIncorrectPage() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        initBookingRequestList();
        bookingRequestListTO.setFrom(Optional.of(-1));
        IncorrectParameterException ex = assertThrows(
                IncorrectParameterException.class, () -> bookingService.getAllItemBookingByIdOwner(
                        bookingRequestListTO
                ));

        assertThat(ex.getMessage(), equalTo("Parameter [from and size] incorrect"));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenIncorrectPage() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        initBookingRequestList();
        bookingRequestListTO.setFrom(Optional.of(-1));
        IncorrectParameterException ex = assertThrows(
                IncorrectParameterException.class, () -> bookingService.getListBookingByIdUser(
                        bookingRequestListTO
                ));

        assertThat(ex.getMessage(), equalTo("Parameter [from and size] incorrect"));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initBookingRequestList();
        assertThrows(EntityNotFoundException.class, () -> bookingService.getListBookingByIdUser(
                bookingRequestListTO
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initBookingRequestList();
        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllItemBookingByIdOwner(
                bookingRequestListTO
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenCaseALL_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllByUserIdBooking(any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        List<BookingResponseTO> result = bookingService.getListBookingByIdUser(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenCaseALL_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllItemsBookingByOwnerId(any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        List<BookingResponseTO> result = bookingService.getAllItemBookingByIdOwner(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenCaseCURRENT_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findCurrentByUserId(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(CURRENT);
        List<BookingResponseTO> result = bookingService.getListBookingByIdUser(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, times(1)).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenCaseCURRENT_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findCurrentItemsBookingByOwnerId(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(CURRENT);
        List<BookingResponseTO> result = bookingService.getAllItemBookingByIdOwner(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, times(1)).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenCasePAST_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllPastByUserId(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(PAST);
        List<BookingResponseTO> result = bookingService.getListBookingByIdUser(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, times(1)).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenCasePAST_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findPastItemsBookingByOwnerId(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(PAST);
        List<BookingResponseTO> result = bookingService.getAllItemBookingByIdOwner(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, times(1)).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenCaseFUTURE_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findFutureByUserId(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(FUTURE);
        List<BookingResponseTO> result = bookingService.getListBookingByIdUser(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, times(1)).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenCaseFUTURE_returnListOfOne() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findFutureItemsBookingByOwnerId(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(FUTURE);
        List<BookingResponseTO> result = bookingService.getAllItemBookingByIdOwner(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, times(1)).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenCaseWAITING_returnListOfOne() {
        initTestVariable(true, true, false);
        BookingState currentState = BookingState.WAITING;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllByUserIdAndStatus(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(currentState);
        List<BookingResponseTO> result = bookingService.getListBookingByIdUser(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenCaseWAITING_returnListOfOne() {
        initTestVariable(true, true, false);
        BookingState currentState = BookingState.WAITING;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findItemsBookingByOwnerIdAndStatus(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(currentState);
        List<BookingResponseTO> result = bookingService.getAllItemBookingByIdOwner(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, times(1)).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

    @Test
    void getListBookingByIdUser_whenCaseREJECTED_returnListOfOne() {
        initTestVariable(true, true, false);
        BookingState currentState = BookingState.REJECTED;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllByUserIdAndStatus(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(currentState);
        List<BookingResponseTO> result = bookingService.getListBookingByIdUser(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByUserIdBooking(any(), any());
        verify(bookingRepository, never()).findCurrentByUserId(any(), any(), any());
        verify(bookingRepository, never()).findAllPastByUserId(any(), any(), any());
        verify(bookingRepository, never()).findFutureByUserId(any(), any(), any());
        verify(bookingRepository, times(1)).findAllByUserIdAndStatus(any(), any(), any());
    }

    @Test
    void getAllItemBookingByIdOwner_whenCaseREJECTED_returnListOfOne() {
        initTestVariable(true, true, false);
        BookingState currentState = BookingState.REJECTED;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(bookingRepository.findItemsBookingByOwnerIdAndStatus(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));

        initBookingRequestList();
        bookingRequestListTO.setState(currentState);
        List<BookingResponseTO> result = bookingService.getAllItemBookingByIdOwner(bookingRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getStart(), notNullValue());
        assertThat(result.get(0).getEnd(), notNullValue());
        assertThat(result.get(0).getItem(), notNullValue());
        assertThat(result.get(0).getItem().getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItem().getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getBooker(), notNullValue());
        assertThat(result.get(0).getBooker().getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllItemsBookingByOwnerId(any(), any());
        verify(bookingRepository, never()).findCurrentItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findPastItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, never()).findFutureItemsBookingByOwnerId(any(), any(), any());
        verify(bookingRepository, times(1)).findItemsBookingByOwnerIdAndStatus(any(), any(), any());
    }

}
