package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingEntityTest {
    @Test
    void equalsBookingEntity() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        BookingEntity booking1 = BookingEntity.builder().id(1L).status(BookingStatus.APPROVED)
                .start(currentTime)
                .end(currentTime)
                .user(UserEntity.builder().id(1L).build())
                .item(ItemEntity.builder().id(1L).build())
                .build();
        BookingEntity booking2 = BookingEntity.builder().id(1L).status(BookingStatus.APPROVED)
                .start(currentTime)
                .end(currentTime)
                .user(UserEntity.builder().id(1L).build())
                .item(ItemEntity.builder().id(1L).build())
                .build();

        assertEquals(booking1, booking2);
    }

}
