package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentResponseTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<CommentResponseTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(false, false, false);
        CommentResponseTO commentResponseTO = CommentResponseTO.builder()
                .id(id)
                .text("TestText")
                .authorName("Author")
                .created(currentTime)
                .build();
        JsonContent<CommentResponseTO> result = json.write(commentResponseTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.text").extractingJsonPathStringValue("$.text");
        assertThat(result).hasJsonPath("$.authorName").extractingJsonPathStringValue("$.authorName");
        assertThat(result).hasJsonPath("$.created").extractingJsonPathStringValue("$.created");
    }
}
