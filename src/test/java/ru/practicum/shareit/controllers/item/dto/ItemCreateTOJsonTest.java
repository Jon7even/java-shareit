package ru.practicum.shareit.controllers.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;
import ru.practicum.shareit.item.dto.ItemCreateTO;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemCreateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemCreateTO> json;

    @Test
    void testSerialize() throws Exception {
        ItemCreateTO itemCreateTO = ItemCreateTO.builder()
                .name("TestName")
                .description("TestDescription")
                .available(true)
                .requestId(id)
                .build();
        JsonContent<ItemCreateTO> result = json.write(itemCreateTO);

        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
        assertThat(result).hasJsonPath("$.available").extractingJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPath("$.requestId").extractingJsonPathNumberValue("$.requestId");
    }
}
