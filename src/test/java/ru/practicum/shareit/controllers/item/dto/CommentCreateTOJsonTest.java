package ru.practicum.shareit.controllers.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;
import ru.practicum.shareit.item.dto.CommentCreateTO;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentCreateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<CommentCreateTO> json;

    @Test
    void testSerialize() throws Exception {
        CommentCreateTO commentCreateTO = CommentCreateTO.builder().text("TestText").build();
        JsonContent<CommentCreateTO> result = json.write(commentCreateTO);

        assertThat(result).hasJsonPath("$.text").extractingJsonPathStringValue("$.text");
    }
}
