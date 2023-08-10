package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.setup.GenericDTOTest;
import ru.practicum.shareit.item.projections.ItemBooking;
import ru.practicum.shareit.user.projections.UserBooker;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingResponseTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<BookingResponseTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(false, false, false);
        LocalDateTime endTime = currentTime.plusHours(1);
        ItemBooking itemBooking = ItemBooking.builder().id(id + 2).name("TestName").build();
        UserBooker userBooker = UserBooker.builder().id(id + 3).build();
        BookingStatus bookingStatus = BookingStatus.WAITING;
        BookingResponseTO bookingResponseTO = BookingResponseTO.builder()
                .id(id)
                .start(currentTime)
                .end(endTime)
                .item(itemBooking)
                .booker(userBooker)
                .status(bookingStatus)
                .build();
        JsonContent<BookingResponseTO> result = json.write(bookingResponseTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.start").extractingJsonPathStringValue("$.start");
        assertThat(result).hasJsonPath("$.end").extractingJsonPathStringValue("$.end");
        assertThat(result).hasJsonPath("$.item.id").extractingJsonPathNumberValue("$.item.id");
        assertThat(result).hasJsonPath("$.item.name").extractingJsonPathStringValue("$.item.name");
        assertThat(result).hasJsonPath("$.booker.id").extractingJsonPathNumberValue("$.booker.id");
        assertThat(result).hasJsonPath("$.status").extractingJsonPathStringValue("$.status");
    }
}



