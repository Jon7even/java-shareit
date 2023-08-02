package ru.practicum.shareit.controllers.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingQueueTO;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;
import ru.practicum.shareit.item.dto.CommentResponseTO;
import ru.practicum.shareit.item.dto.ItemResponseBookingAndCommentTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemResponseBookingAndCommentTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemResponseBookingAndCommentTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(true, false, false);
        BookingQueueTO lastBooking = BookingQueueTO.builder().id(id + 1).bookerId(id + 4).build();
        BookingQueueTO nextBooking = BookingQueueTO.builder().id(id + 3).bookerId(id + 2).build();
        List<CommentResponseTO> commentsDTO = List.of(commentDTO);
        ItemResponseBookingAndCommentTO itemResponseBookingAndCommentTO = ItemResponseBookingAndCommentTO.builder()
                .id(id)
                .name("TestName")
                .description("TestDescription")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentsDTO)
                .build();
        JsonContent<ItemResponseBookingAndCommentTO> result = json.write(itemResponseBookingAndCommentTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
        assertThat(result).hasJsonPath("$.available").extractingJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPath("$.lastBooking").extractingJsonPathValue("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking").extractingJsonPathValue("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments").extractingJsonPathArrayValue("$.comments");
    }
}
