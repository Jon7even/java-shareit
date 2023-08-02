package ru.practicum.shareit.controllers.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingRequestListTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<BookingRequestListTO> json;

    @Test
    void testSerialize() throws Exception {
        initOptionalVariable();
        BookingState bookingState = BookingState.ALL;
        BookingRequestListTO bookingRequestListTO = BookingRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .state(bookingState)
                .build();
        JsonContent<BookingRequestListTO> result = json.write(bookingRequestListTO);

        assertThat(result).hasJsonPath("$.idUser").extractingJsonPathNumberValue("$.idUser");
        assertThat(result).hasJsonPath("$.from").extractingJsonPathNumberValue("$.from");
        assertThat(result).hasJsonPath("$.size").extractingJsonPathNumberValue("$.size");
        assertThat(result).hasJsonPath("$.state").extractingJsonPathStringValue("$.state");
    }
}