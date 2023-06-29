package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenericControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserService userService;

    protected User firstUser;

    protected User secondUser;

    protected User thirdUser;

    void initUsers() {
        firstUser = User.builder()
                .name("firstUser")
                .email("firstUser@yandex.ru")
                .build();
        secondUser = User.builder()
                .id(1)
                .name("secondUser")
                .email("secondUser@yandex.ru")
                .build();
        thirdUser = User.builder()
                .build();
    }
}
