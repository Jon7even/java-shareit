package ru.practicum.shareit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.entity.Item;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.constants.NamesJsonResponse.ERROR_M_VALIDATION;
import static ru.practicum.shareit.constants.NamesParametersInController.X_COUNT_ITEMS;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

public class ItemControllerTest extends GenericControllerTest {
    private ItemRequestCreateDTO firstItem;

    private ItemRequestCreateDTO secondItem;

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
        userService.createUser(firstUser);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(FIRST_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItem"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(true));
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
        userService.createUser(firstUser);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

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
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(FIRST_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(true));

        firstItem.setDescription("description_1_AfterUpdate");

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1_AfterUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(true));

        firstItem.setAvailable(false);

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1_AfterUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(false));

        firstItem.setName("firstItem");
        firstItem.setDescription("description_1");
        firstItem.setAvailable(true);

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItem"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(true));
    }

    @Test
    @DisplayName("Поиск Итема по [ID]")
    void shouldGetItemById_thenStatus200AndStatus404AndStatus400() throws Exception {
        userService.createUser(firstUser);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstItem"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("description_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("available").value(true));

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

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID));

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(2)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].name").value(secondItem.getName()));

        thirdItem.setName("thirdItem");
        thirdItem.setDescription("description_3");
        thirdItem.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(3)))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].name").value(secondItem.getName()))
                .andExpect(jsonPath("$[2].id").value(FIRST_ID + 2))
                .andExpect(jsonPath("$[2].name").value("thirdItem"))
                .andExpect(jsonPath("$[2].description").value("description_3"));
    }

    @Test
    @DisplayName("Получить итемы по строке поиска")
    void shouldGetSearchItemsByRequest_thenStatus200() throws Exception {
        userService.createUser(firstUser);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        thirdItem.setName("thirdItem");
        thirdItem.setDescription("description_3");
        thirdItem.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/items/search?text={text}", "")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", " ")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", "thirdItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(get("/items/search?text={text}", "thirdItem")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/items/search?text={text}", "Item")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        secondItem.setAvailable(true);
        mockMvc.perform(patch("/items/{itemId}", FIRST_ID + 1)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", "Item")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(3)))
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/items/search?text={text}", "description_1")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
