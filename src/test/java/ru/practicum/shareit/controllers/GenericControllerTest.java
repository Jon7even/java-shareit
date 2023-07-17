package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestCreateDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

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

    protected UserRequestCreateDTO firstUser;

    protected UserRequestCreateDTO secondUser;

    protected UserRequestCreateDTO thirdUser;

    protected ItemRequestCreateDTO firstItem;

    protected ItemRequestCreateDTO secondItem;

    protected BookingRequestCreateDTO firstBooking;

    protected BookingRequestCreateDTO secondBooking;

    protected BookingRequestCreateDTO thirdBooking;

    protected Item thirdItem;

    void initUsers() {
        firstUser = UserRequestCreateDTO.builder()
                .name("firstUser")
                .email("firstUser@yandex.ru")
                .build();
        secondUser = UserRequestCreateDTO.builder()
                .name("secondUser")
                .email("secondUser@yandex.ru")
                .build();
        thirdUser = UserRequestCreateDTO.builder()
                .build();
    }

    void initBookings() {
        firstBooking = BookingRequestCreateDTO.builder()
                .itemId(FIRST_ID)
                .build();
        secondBooking = BookingRequestCreateDTO.builder()
                .itemId(FIRST_ID)
                .build();
        thirdBooking = BookingRequestCreateDTO.builder()
                .itemId(FIRST_ID)
                .build();
    }

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
}
