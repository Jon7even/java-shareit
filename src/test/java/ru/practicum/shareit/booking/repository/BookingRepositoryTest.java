package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.setup.GenericRepositoryTest;
import ru.practicum.shareit.utils.CommonValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingRepositoryTest extends GenericRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private ItemEntity itemEntityInDb;

    private BookingEntity bookingEntityInDb;

    private final BookingStatus bookingStatus = BookingStatus.APPROVED;

    private final Pageable page = CommonValidator.getPageRequest(
            Optional.of(0), Optional.of(20), Optional.empty()
    );

    @BeforeEach
    void setUp_Two() {
        initTestVariable(true, true, false);
        itemEntityInDb = itemRepository.save(itemEntity);
        bookingEntityInDb = bookingRepository.save(bookingEntity);
    }

    @Test
    void saveBooking() {
        assertNotNull(bookingEntityInDb);
        assertEquals(bookingEntityInDb.getId(), bookingEntity.getId());
        assertEquals(bookingEntityInDb.getStart(), bookingEntity.getStart());
        assertEquals(bookingEntityInDb.getEnd(), bookingEntity.getEnd());
        assertEquals(bookingEntityInDb.getItem(), bookingEntity.getItem());
        assertEquals(bookingEntityInDb.getUser(), bookingEntity.getUser());
        assertEquals(bookingEntityInDb.getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findBooking() {
        Optional<BookingEntity> result = bookingRepository.findById(bookingEntity.getId());

        assertEquals(result.get().getId(), bookingEntity.getId());
        assertEquals(result.get().getStart(), bookingEntity.getStart());
        assertEquals(result.get().getEnd(), bookingEntity.getEnd());
        assertEquals(result.get().getItem(), bookingEntity.getItem());
        assertEquals(result.get().getUser(), bookingEntity.getUser());
        assertEquals(result.get().getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findAllByUserIdBooking() {
        List<BookingEntity> result = bookingRepository.findAllByUserIdBooking(userInDB, page);

        assertEquals(result.get(0).getId(), bookingEntity.getId());
        assertEquals(result.get(0).getStart(), bookingEntity.getStart());
        assertEquals(result.get(0).getEnd(), bookingEntity.getEnd());
        assertEquals(result.get(0).getItem(), bookingEntity.getItem());
        assertEquals(result.get(0).getUser(), bookingEntity.getUser());
        assertEquals(result.get(0).getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findAllByUserIdAndStatus() {
        List<BookingEntity> result = bookingRepository.findAllByUserIdAndStatus(userInDB, bookingStatus, page);

        assertEquals(result.get(0).getId(), bookingEntity.getId());
        assertEquals(result.get(0).getStart(), bookingEntity.getStart());
        assertEquals(result.get(0).getEnd(), bookingEntity.getEnd());
        assertEquals(result.get(0).getItem(), bookingEntity.getItem());
        assertEquals(result.get(0).getUser(), bookingEntity.getUser());
        assertEquals(result.get(0).getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findAllItemsBookingByOwnerId() {
        List<BookingEntity> result = bookingRepository.findAllItemsBookingByOwnerId(userInDB, page);

        assertEquals(result.get(0).getId(), bookingEntity.getId());
        assertEquals(result.get(0).getStart(), bookingEntity.getStart());
        assertEquals(result.get(0).getEnd(), bookingEntity.getEnd());
        assertEquals(result.get(0).getItem(), bookingEntity.getItem());
        assertEquals(result.get(0).getUser(), bookingEntity.getUser());
        assertEquals(result.get(0).getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findItemsBookingByOwnerIdAndStatus() {
        List<BookingEntity> result = bookingRepository.findItemsBookingByOwnerIdAndStatus(userInDB, bookingStatus, page);

        assertEquals(result.get(0).getId(), bookingEntity.getId());
        assertEquals(result.get(0).getStart(), bookingEntity.getStart());
        assertEquals(result.get(0).getEnd(), bookingEntity.getEnd());
        assertEquals(result.get(0).getItem(), bookingEntity.getItem());
        assertEquals(result.get(0).getUser(), bookingEntity.getUser());
        assertEquals(result.get(0).getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findCurrentItemsBookingByOwnerId() {
        List<BookingEntity> result = bookingRepository.findCurrentItemsBookingByOwnerId(userInDB, currentTime, page);

        assertEquals(result.get(0).getId(), bookingEntity.getId());
        assertEquals(result.get(0).getStart(), bookingEntity.getStart());
        assertEquals(result.get(0).getEnd(), bookingEntity.getEnd());
        assertEquals(result.get(0).getItem(), bookingEntity.getItem());
        assertEquals(result.get(0).getUser(), bookingEntity.getUser());
        assertEquals(result.get(0).getStatus(), bookingEntity.getStatus());
    }

    @Test
    void findByItemOrderByStart() {
        List<BookingEntity> result = bookingRepository.findByItemOrderByStart(itemEntityInDb);

        assertEquals(result.get(0).getId(), bookingEntity.getId());
        assertEquals(result.get(0).getStart(), bookingEntity.getStart());
        assertEquals(result.get(0).getEnd(), bookingEntity.getEnd());
        assertEquals(result.get(0).getItem(), bookingEntity.getItem());
        assertEquals(result.get(0).getUser(), bookingEntity.getUser());
        assertEquals(result.get(0).getStatus(), bookingEntity.getStatus());
    }

}
