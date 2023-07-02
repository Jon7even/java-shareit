package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

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

    protected static long FIRST_ID = 1;

    protected static String ERROR_NAME = "$.errorMessage";

    protected static String ERROR_M_USER_NOT_FOUND = "[User with [idUser=2]] not found";

    protected static String ERROR_M_ITEM_NOT_FOUND = "[Item with [idItem=2]] not found";

    protected static String ERROR_M_HEADER_USER_ID = "Parameter [" + X_HEADER_USER_ID + "] incorrect";

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
