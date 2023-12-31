package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.shareit.setup.GenericControllerTest;
import ru.practicum.shareit.user.dto.UserResponseTO;

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

public class UserControllerTest extends GenericControllerTest {
    @BeforeEach
    void setUp() {
        initUsers();
    }

    @Test
    @DisplayName("Пользователь должен создаться с релевантными полями [createUser]")
    void shouldCreateUser_thenStatus201() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("firstUser"))
                .andExpect(jsonPath("email").value("firstUser@yandex.ru"));
    }

    @Test
    @DisplayName("Пользователь не должен создаться [createUser]")
    void shouldNotCreateUser_thenStatus400() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        thirdUser.setName("Name");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        thirdUser.setEmail("12345");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        thirdUser.setName(null);
        thirdUser.setEmail("thirdUser@yandex.ru");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));
    }

    @Test
    @DisplayName("Поиск пользователя по [ID] [getUserById]")
    void shouldGetUserById_thenStatus200AndStatus404() throws Exception {
        userService.createUser(firstUser);

        mockMvc.perform(get("/users/{id}", FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value(firstUser.getName()))
                .andExpect(jsonPath("email").value(firstUser.getEmail()));

        mockMvc.perform(get("/users/{id}", FIRST_ID + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));
    }

    @Test
    @DisplayName("Поиск пользователя по неправильному [ID] [getUserById]")
    void shouldNotGetUserById_thenStatus404() throws Exception {
        mockMvc.perform(get("/users/{id}", -1))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/{id}", 0))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Пользователь должен обновить поля частично [updateUserById]")
    void shouldUserUseMethodPatch_thenStatus200() throws Exception {
        userService.createUser(secondUser);

        mockMvc.perform(patch("/users/{userId}", FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        thirdUser.setName("thirdUser");

        mockMvc.perform(patch("/users/{userId}", FIRST_ID)
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("thirdUser"))
                .andExpect(jsonPath("email").value("secondUser@yandex.ru"));

        thirdUser.setEmail("thirdUser@yandex.ru");

        mockMvc.perform(patch("/users/{userId}", FIRST_ID)
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("thirdUser"))
                .andExpect(jsonPath("email").value("thirdUser@yandex.ru"));
    }

    @Test
    @DisplayName("Пользователь должен удалиться по [ID] [removeUserById]")
    void shouldDeleteUserById_thenStatus204And404() throws Exception {
        userService.createUser(secondUser);

        mockMvc.perform(delete("/users/{id}", FIRST_ID + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        mockMvc.perform(delete("/users/{id}", FIRST_ID))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/users/{id}", FIRST_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить пользователя по [ID] и все его итемы [removeUserById]")
    void shouldDeleteUserByIdAndShouldDeleteItem_thenStatus204And404() throws Exception {
        initItems();
        userService.createUser(firstUser);
        itemService.createItem(firstItem, Optional.of(1L));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/users/{id}", FIRST_ID + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        mockMvc.perform(delete("/users/{id}", FIRST_ID))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/users/{id}", FIRST_ID))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/items/search?text={text}", "Item")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получить всех пользователей [getAllUsers]")
    void shouldGetAllUsers_thenStatus200AndResultTwoUsers() throws Exception {
        UserResponseTO user1 = userService.createUser(firstUser);
        UserResponseTO user2 = userService.createUser(secondUser);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(2)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()));
    }

}
