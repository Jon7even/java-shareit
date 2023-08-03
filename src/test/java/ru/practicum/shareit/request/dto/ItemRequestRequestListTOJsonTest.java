package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestRequestListTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemRequestRequestListTO> json;

    @Test
    void testSerialize() throws Exception {
        initOptionalVariable();
        ItemRequestRequestListTO itemRequestListTO = ItemRequestRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .build();
        JsonContent<ItemRequestRequestListTO> result = json.write(itemRequestListTO);

        assertThat(result).hasJsonPath("$.idUser").extractingJsonPathNumberValue("$.idUser");
        assertThat(result).hasJsonPath("$.from").extractingJsonPathNumberValue("$.from");
        assertThat(result).hasJsonPath("$.size").extractingJsonPathNumberValue("$.size");
    }
}