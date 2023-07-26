package ru.practicum.shareit.controllers.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.controllers.setup.controller.GenericControllerTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

public class BookingControllerTest extends GenericControllerTest {

    @Autowired
    protected BookingService bookingService;

    @BeforeEach
    void setUp() {
        initItems();
        initUsers();
        initBookings();
    }

    @Test
    @DisplayName("Бронирование должно создаться с релевантными полями")
    void shouldCreateBooking_thenStatus201() throws Exception {
        userService.createUser(firstUser);
        userService.createUser(secondUser);
        itemService.createItem(firstItem, Optional.of(FIRST_ID));

        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
        firstBooking.setStart(start);
        firstBooking.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("start").value(start.toString()))
                .andExpect(jsonPath("end").value(end.toString()))
                .andExpect(jsonPath("$.item.id").value(FIRST_ID))
                .andExpect(jsonPath("$.item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$.booker.id").value(FIRST_ID + 1))
                .andExpect(jsonPath("status").value(BookingStatus.WAITING.toString()));
    }

}