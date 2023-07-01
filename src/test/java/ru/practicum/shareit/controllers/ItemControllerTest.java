package ru.practicum.shareit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.entity.Item;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerTest extends GenericControllerTest {
    protected ItemRequestCreateDTO firstItem;

    protected ItemRequestCreateDTO secondItem;

    protected Item thirdItem;

    void initItems() {
        firstItem = ItemRequestCreateDTO.builder()
                .name("firstItem")
                .description("description_1")
                .available(true)
                .build();
        secondItem = ItemRequestCreateDTO.builder()
                .name("secondItem")
                .description("description_2")
                .available(false)
                .build();
        thirdItem = Item.builder()
                .build();
    }

    @BeforeEach
    void setUp() {
        initItems();
        initUsers();
    }

    @Test
    @DisplayName("Итем должен создаться с релевантными полями")
    void shouldCreateItem_thenStatus201() throws Exception {
        Long idUser = userService.createUser(firstUser).getId();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", idUser)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItem"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(true));
    }

    @Test
    @DisplayName("Итем не должен создаться")
    void shouldNotCreateItem_thenStatus400And404() throws Exception {
        Long idUser = userService.createUser(firstUser).getId();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", idUser)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", idUser + 1)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        secondItem.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", idUser)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        secondItem.setAvailable(true);
        secondItem.setName(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", idUser)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        secondItem.setAvailable(true);
        secondItem.setName("name");
        secondItem.setDescription(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", idUser)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
