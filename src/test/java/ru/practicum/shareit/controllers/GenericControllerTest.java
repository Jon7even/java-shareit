package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemCreateTO;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.service.UserService;

import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenericControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected BookingService bookingService;

    protected static long FIRST_ID = 1;

    protected static String ERROR_NAME = "$.errorMessage";

    protected static String ERROR_M_USER_NOT_FOUND = "[User with [idUser=2]] not found";

    protected static String ERROR_M_ITEM_NOT_FOUND = "[Item with [idItem=2]] not found";

    protected static String ERROR_M_HEADER_USER_ID = "Parameter [" + X_HEADER_USER_ID + "] incorrect";

    protected UserCreateTO firstUser;

    protected UserCreateTO secondUser;

    protected UserCreateTO thirdUser;

    protected ItemCreateTO firstItem;

    protected ItemCreateTO secondItem;

    protected BookingCreateTO firstBooking;

    protected BookingCreateTO secondBooking;

    protected BookingCreateTO thirdBooking;

    protected ItemEntity thirdItem;

    void initUsers() {
        firstUser = UserCreateTO.builder()
                .name("firstUser")
                .email("firstUser@yandex.ru")
                .build();
        secondUser = UserCreateTO.builder()
                .name("secondUser")
                .email("secondUser@yandex.ru")
                .build();
        thirdUser = UserCreateTO.builder()
                .build();
    }

    void initBookings() {
        firstBooking = BookingCreateTO.builder()
                .itemId(FIRST_ID)
                .build();
        secondBooking = BookingCreateTO.builder()
                .itemId(FIRST_ID)
                .build();
        thirdBooking = BookingCreateTO.builder()
                .itemId(FIRST_ID)
                .build();
    }

    void initItems() {
        firstItem = ItemCreateTO.builder()
                .name("firstItem")
                .description("description_1")
                .available(true)
                .build();
        secondItem = ItemCreateTO.builder()
                .name("secondItem")
                .description("description_2")
                .available(false)
                .build();
        thirdItem = ItemEntity.builder()
                .build();
    }
}
