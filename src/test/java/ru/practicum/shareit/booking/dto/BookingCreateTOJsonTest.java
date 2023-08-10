package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingCreateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<BookingCreateTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(false, false, false);
        LocalDateTime endTime = currentTime.plusHours(1);
        BookingCreateTO bookingCreateTO = BookingCreateTO.builder()
                .itemId(id)
                .start(currentTime)
                .end(endTime)
                .build();
        JsonContent<BookingCreateTO> result = json.write(bookingCreateTO);

        assertThat(result).hasJsonPath("$.itemId").extractingJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPath("$.start").extractingJsonPathStringValue("$.start");
        assertThat(result).hasJsonPath("$.end").extractingJsonPathStringValue("$.end");
    }
}