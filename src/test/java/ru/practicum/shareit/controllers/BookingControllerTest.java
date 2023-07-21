package ru.practicum.shareit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

public class BookingControllerTest extends GenericControllerTest {

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
        itemService.createItem(firstItem, Optional.of(FIRST_ID));

        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
        firstBooking.setStart(start);
        firstBooking.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(FIRST_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("start").value(start.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("end").value(end.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id").value(FIRST_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value(firstItem.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(FIRST_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(BookingStatus.WAITING.toString()));
    }
}