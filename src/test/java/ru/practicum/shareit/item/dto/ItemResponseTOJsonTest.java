package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemResponseTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemResponseTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(false, false, false);
        ItemResponseTO itemResponseTO = ItemResponseTO.builder()
                .id(id)
                .name("TestName")
                .description("TestDescription")
                .available(true)
                .requestId(id + 1)
                .build();
        JsonContent<ItemResponseTO> result = json.write(itemResponseTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
        assertThat(result).hasJsonPath("$.available").extractingJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPath("$.requestId").extractingJsonPathNumberValue("$.requestId");
    }
}