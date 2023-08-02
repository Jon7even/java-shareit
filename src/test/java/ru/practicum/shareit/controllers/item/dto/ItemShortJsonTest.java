package ru.practicum.shareit.controllers.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;
import ru.practicum.shareit.item.projections.ItemShort;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemShortJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemShort> json;

    @Test
    void testSerialize() throws Exception {
        ItemShort itemShort = ItemShort.builder()
                .id(id).name("TestName")
                .description("TestDescription")
                .available(true)
                .build();
        JsonContent<ItemShort> result = json.write(itemShort);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
        assertThat(result).hasJsonPath("$.available").extractingJsonPathBooleanValue("$.available");
    }
}
