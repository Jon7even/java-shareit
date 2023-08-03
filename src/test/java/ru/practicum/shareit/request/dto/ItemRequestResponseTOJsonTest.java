package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;
import ru.practicum.shareit.item.dto.ItemResponseTO;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestResponseTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemRequestResponseTO> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(true, false, false);
        List<ItemResponseTO> listItemsTO = List.of(ItemMapper.INSTANCE.toDTOResponseFromEntity(itemEntity));
        ItemRequestResponseTO itemRequestResponseTO = ItemRequestResponseTO.builder()
                .id(id)
                .description("TestDescription")
                .created(currentTime)
                .items(listItemsTO)
                .build();
        JsonContent<ItemRequestResponseTO> result = json.write(itemRequestResponseTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.description").extractingJsonPathStringValue("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items").extractingJsonPathArrayValue("$.items");
    }
}


