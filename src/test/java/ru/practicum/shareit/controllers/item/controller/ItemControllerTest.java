package ru.practicum.shareit.controllers.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.shareit.controllers.setup.controller.GenericControllerTest;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static ru.practicum.shareit.constants.NamesJsonResponse.ERROR_M_VALIDATION;
import static ru.practicum.shareit.constants.NamesParametersInController.X_COUNT_ITEMS;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

public class ItemControllerTest extends GenericControllerTest {
    @BeforeEach
    void setUp() {
        initItems();
        initUsers();
    }

    @Test
    @DisplayName("Итем должен создаться с релевантными полями")
    void shouldCreateItem_thenStatus201() throws Exception {
        userService.createUser(firstUser);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));
    }

    @Test
    @DisplayName("Итем не должен создаться")
    void shouldNotCreateItem_thenStatus400And404() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        secondItem.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        secondItem.setAvailable(true);
        secondItem.setName(null);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        secondItem.setAvailable(true);
        secondItem.setName("name");
        secondItem.setDescription(null);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));
    }

    @Test
    @DisplayName("Итем должен обновить поля частично")
    void shouldItemUseMethodPatch_thenStatus200() throws Exception {
        Long idUser = 1L;
        userService.createUser(firstUser);
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        firstItem.setName("firstItemAfterUpdate");

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));

        firstItem.setDescription("description_1_AfterUpdate");

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(jsonPath("description").value("description_1_AfterUpdate"))
                .andExpect(jsonPath("available").value(true));

        firstItem.setAvailable(false);

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(jsonPath("description").value("description_1_AfterUpdate"))
                .andExpect(jsonPath("available").value(false));

        firstItem.setName("firstItem");
        firstItem.setDescription("description_1");
        firstItem.setAvailable(true);

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));
    }

    @Test
    @DisplayName("Поиск Итема по [ID]")
    void shouldGetItemById_thenStatus200AndStatus404AndStatus400() throws Exception {
        Long idUser = 1L;
        userService.createUser(firstUser);
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID + 1)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_ITEM_NOT_FOUND));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));
    }

    @Test
    @DisplayName("Получить все итемы пользователя по [" + X_HEADER_USER_ID + "]")
    void shouldGetAllItemsByIdUser_thenStatus200() throws Exception {
        userService.createUser(firstUser);
        userService.createUser(secondUser);

        itemService.createItem(firstItem, Optional.of(1L));
        itemService.createItem(secondItem, Optional.of(2L));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].name").value(secondItem.getName()));
    }

    @Test
    @DisplayName("Получить итемы по строке поиска")
    void shouldGetSearchItemsByRequest_thenStatus200() throws Exception {
        Long idUser = 1L;
        userService.createUser(firstUser);

        itemService.createItem(firstItem, Optional.of(idUser));
        mockMvc.perform(get("/items/search?text={text}", "Item")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()));

        itemService.createItem(secondItem, Optional.of(idUser));
        mockMvc.perform(get("/items/search?text={text}", "description_2")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        secondItem.setAvailable(true);
        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/items/search?text={text}", "description_2")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(secondItem.getName()));

        thirdItem.setName("thirdItem");
        thirdItem.setDescription("thirdItem");
        thirdItem.setAvailable(true);
        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/items/search?text={text}", "rdIt")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("thirdItem"));

        mockMvc.perform(get("/items/search?text={text}", "")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", " ")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", "thirdItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));
    }

    @Test
    @DisplayName("Итем должен удалиться по [ID]")
    void shouldDeleteItemById_thenStatus204And404() throws Exception {
        Long idUser = 1L;
        userService.createUser(firstUser);
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(delete("/items/{id}", FIRST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(delete("/items/{id}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/items/{id}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound());
    }

}
