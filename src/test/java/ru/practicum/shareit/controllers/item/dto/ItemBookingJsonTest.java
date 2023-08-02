package ru.practicum.shareit.controllers.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.controllers.setup.GenericDTOTest;
import ru.practicum.shareit.item.projections.ItemBooking;


import static org.assertj.core.api.Assertions.assertThat;

public class ItemBookingJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<ItemBooking> json;

    @Test
    void testSerialize() throws Exception {
        ItemBooking itemBooking = ItemBooking.builder().id(id).name("TestName").build();
        JsonContent<ItemBooking> result = json.write(itemBooking);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
    }
}
