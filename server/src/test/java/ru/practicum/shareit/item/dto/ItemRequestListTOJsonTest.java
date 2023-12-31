package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestListTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemRequestListTO> json;

    @Test
    void testSerialize() throws Exception {
        initOptionalVariable();
        ItemRequestListTO itemRequestListTO = ItemRequestListTO.builder()
                .idUser(id)
                .from(fromOptional)
                .size(sizeOptional)
                .text(textOptional)
                .build();
        JsonContent<ItemRequestListTO> result = json.write(itemRequestListTO);

        assertThat(result).hasJsonPath("$.idUser").extractingJsonPathNumberValue("$.idUser");
        assertThat(result).hasJsonPath("$.from").extractingJsonPathNumberValue("$.from");
        assertThat(result).hasJsonPath("$.size").extractingJsonPathNumberValue("$.size");
        assertThat(result).hasJsonPath("$.text").extractingJsonPathStringValue("$.text");
    }
}