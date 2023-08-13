package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestCreateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemRequestCreateTO> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestCreateTO itemRequestCreateTO = ItemRequestCreateTO.builder().description("TestDescription").build();
        JsonContent<ItemRequestCreateTO> result = json.write(itemRequestCreateTO);

        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
    }
}

