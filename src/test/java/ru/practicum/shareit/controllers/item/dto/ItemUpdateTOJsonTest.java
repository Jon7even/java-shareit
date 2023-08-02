package ru.practicum.shareit.controllers.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;
import ru.practicum.shareit.item.dto.ItemUpdateTO;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemUpdateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemUpdateTO> json;

    @Test
    void testSerialize() throws Exception {
        ItemUpdateTO itemUpdateTO = ItemUpdateTO.builder()
                .name("TestName")
                .description("TestDescription")
                .available(true)
                .build();
        JsonContent<ItemUpdateTO> result = json.write(itemUpdateTO);

        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
        assertThat(result).hasJsonPath("$.available").extractingJsonPathBooleanValue("$.available");
    }
}
