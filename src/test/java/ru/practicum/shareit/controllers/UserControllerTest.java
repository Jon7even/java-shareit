package ru.practicum.shareit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.entity.User;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends GenericControllerTest {
    @BeforeEach
    void setUp() {
        initUsers();
    }

    @Test
    @DisplayName("Пользователь должен создаться с релевантными полями")
    void shouldCreateUser_thenStatus201() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("firstUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("firstUser@yandex.ru"));
    }

    @Test
    @DisplayName("Пользователь не должен создаться")
    void shouldNotCreateUser_thenStatus500() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

/*        thirdUser.setName("Name");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());*/
    }

    @Test
    @DisplayName("Пользователь должен обновить поля частично")
    void shouldUserUseMethodPatch_thenStatus200() throws Exception {
        User addUser = userService.createUser(secondUser);

        mockMvc.perform(patch("/users/{userId}", addUser.getId())
                        .content(objectMapper.writeValueAsString(secondUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        thirdUser.setName("thirdUser");

        mockMvc.perform(patch("/users/{userId}", addUser.getId())
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(addUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("thirdUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("secondUser@yandex.ru"));

        thirdUser.setEmail("thirdUser@yandex.ru");

        mockMvc.perform(patch("/users/{userId}", addUser.getId())
                        .content(objectMapper.writeValueAsString(thirdUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(addUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("thirdUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("thirdUser@yandex.ru"));
    }

    @Test
    @DisplayName("Поиск пользователя по [ID]")
    void shouldGetUserById_thenStatus200() throws Exception {
        User addUser = userService.createUser(firstUser);

        mockMvc.perform(get("/users/{id}", addUser.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(addUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(addUser.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(addUser.getEmail()));
    }

    @Test
    @DisplayName("Поиск пользователя по неправильному [ID]")
    void shouldNotGetUserById_thenStatus404() throws Exception {
        mockMvc.perform(get("/users/{id}", -1))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/{id}", 0))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Пользователь должен удалиться по [ID]")
    void shouldDeleteUserById_thenStatus204() throws Exception {
        User removeUser = userService.createUser(secondUser);

        mockMvc.perform(delete("/users/{id}", removeUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/users/{id}", removeUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получить всех пользователей")
    void shouldGetAllUsers_thenStatus200AndResultTwoUsers() throws Exception {
        User user1 = userService.createUser(firstUser);
        User user2 = userService.createUser(secondUser);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()));
    }

}
