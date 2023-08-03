package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingQueueTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<BookingQueueTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(true, false, false);
        BookingQueueTO bookingQueueTO = BookingQueueTO.builder().id(id).bookerId(id + 2).build();
        JsonContent<BookingQueueTO> result = json.write(bookingQueueTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.bookerId").extractingJsonPathNumberValue("$.bookerId");
    }
}

