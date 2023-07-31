package ru.practicum.shareit.controllers.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.controllers.setup.controller.GenericControllerTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.constants.NamesJsonResponse.ERROR_M_VALIDATION;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

public class BookingControllerTest extends GenericControllerTest {

    @BeforeEach
    void setUp() {
        initItems();
        initUsers();
        initBookings();
        userService.createUser(firstUser);
        userService.createUser(secondUser);
        itemService.createItem(firstItem, Optional.of(FIRST_ID + 1));
    }

    @Test
    @DisplayName("Бронирование должно создаться с релевантными полями [createBooking]")
    void shouldCreateBooking_thenStatus201() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
        firstBooking.setStart(start);
        firstBooking.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("start").value(start.toString()))
                .andExpect(jsonPath("end").value(end.toString()))
                .andExpect(jsonPath("$.item.id").value(FIRST_ID))
                .andExpect(jsonPath("$.item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$.booker.id").value(FIRST_ID))
                .andExpect(jsonPath("status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    @DisplayName("Бронирование не должно создаться [createBooking]")
    void shouldNotCreateBooking_thenStatus400And404() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        firstBooking.setStart(LocalDateTime.now().plusMinutes(5));
        firstBooking.setEnd(LocalDateTime.now().plusHours(1));

        String errorUserOwner = "[Item where you are not the owner] not found";

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(errorUserOwner));

        String errorUserNotFound = "[User with [idUser=4]] not found";

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID + 3)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(errorUserNotFound));

        firstBooking.setItemId(2L);

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_ITEM_NOT_FOUND));

        firstBooking.setStart(LocalDateTime.now().minusHours(1));

        mockMvc.perform(post("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));
    }

    @Test
    @DisplayName("Поиск Бронирования по [ID] [getBookingById]")
    void shouldGetBookingById_thenStatus200AndStatus404AndStatus400() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
        firstBooking.setStart(start);
        firstBooking.setEnd(end);
        bookingService.createBooking(firstBooking, Optional.of(FIRST_ID));

        mockMvc.perform(get("/bookings/{bookingId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("start").value(start.toString()))
                .andExpect(jsonPath("end").value(end.toString()))
                .andExpect(jsonPath("$.item.id").value(FIRST_ID))
                .andExpect(jsonPath("$.item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$.booker.id").value(FIRST_ID))
                .andExpect(jsonPath("status").value(BookingStatus.WAITING.toString()));

        String errorBookingNotFound = "[Booking with [idBooking=2]] not found";

        mockMvc.perform(get("/bookings/{bookingId}", FIRST_ID + 1)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(errorBookingNotFound));

        mockMvc.perform(get("/bookings/{bookingId}", FIRST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        String errorUserNotFound = "[User with [idUser=4]] not found";

        mockMvc.perform(get("/bookings/{bookingId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID + 3))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(errorUserNotFound));
    }

    @Test
    @DisplayName("Владелец вещи должен подтвердить бронирование [confirmBooking]")
    void shouldBookingUseMethodPatchTrue_thenStatus200And400() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
        firstBooking.setStart(start);
        firstBooking.setEnd(end);
        bookingService.createBooking(firstBooking, Optional.of(FIRST_ID));

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID, true)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("start").value(start.toString()))
                .andExpect(jsonPath("end").value(end.toString()))
                .andExpect(jsonPath("$.item.id").value(FIRST_ID))
                .andExpect(jsonPath("$.item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$.booker.id").value(FIRST_ID))
                .andExpect(jsonPath("status").value(BookingStatus.APPROVED.toString()));

        String errorBookingAlreadyApproved = "Parameter [REJECTED] incorrect";

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID, false)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(errorBookingAlreadyApproved));
    }

    @Test
    @DisplayName("Владелец вещи должен отклонить бронирование [confirmBooking]")
    void shouldBookingUseMethodPatchFalse_thenStatus200() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
        firstBooking.setStart(start);
        firstBooking.setEnd(end);
        bookingService.createBooking(firstBooking, Optional.of(FIRST_ID));

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID, false)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("start").value(start.toString()))
                .andExpect(jsonPath("end").value(end.toString()))
                .andExpect(jsonPath("$.item.id").value(FIRST_ID))
                .andExpect(jsonPath("$.item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$.booker.id").value(FIRST_ID))
                .andExpect(jsonPath("status").value(BookingStatus.REJECTED.toString()));
    }

    @Test
    @DisplayName("Получить список бронирований владельцем бронирования [getAllItemsByUserId]")
    void shouldGetListBookingByIdBooker_withoutParameterPageable_thenStatus200() throws Exception {
        secondItem.setAvailable(true);
        itemService.createItem(secondItem, Optional.of(FIRST_ID + 1));
        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);

        firstBooking.setStart(start);
        firstBooking.setEnd(end);
        bookingService.createBooking(firstBooking, Optional.of(FIRST_ID));
        secondBooking.setStart(start);
        secondBooking.setEnd(end);
        bookingService.createBooking(secondBooking, Optional.of(FIRST_ID));

        mockMvc.perform(get("/bookings?state={state}", "ALL")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));

        mockMvc.perform(get("/bookings")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));

        mockMvc.perform(get("/bookings?state={state}", "WAITING")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));

        mockMvc.perform(get("/bookings?state={state}", "CURRENT")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID, true)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID + 1, true)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(secondBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings?state={state}", "FUTURE")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));
    }

    @Test
    @DisplayName("Получить список бронирований владельцем итемов [getAllItemBookingByIdOwner]")
    void shouldGetListBookingByIdOwnerItem_withoutParameterPageable_thenStatus200() throws Exception {
        secondItem.setAvailable(true);
        itemService.createItem(secondItem, Optional.of(FIRST_ID + 1));
        LocalDateTime start = LocalDateTime.now().plusMinutes(5).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);

        firstBooking.setStart(start);
        firstBooking.setEnd(end);
        bookingService.createBooking(firstBooking, Optional.of(FIRST_ID));
        secondBooking.setStart(start);
        secondBooking.setEnd(end);
        bookingService.createBooking(secondBooking, Optional.of(FIRST_ID));

        mockMvc.perform(get("/bookings/owner?state={state}", "ALL")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));

        mockMvc.perform(get("/bookings/owner?state={state}", "WAITING")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));

        mockMvc.perform(get("/bookings/owner?state={state}", "CURRENT")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID, true)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", FIRST_ID + 1, true)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(secondBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner?state={state}", "FUTURE")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].start").value(start.toString()))
                .andExpect(jsonPath("$[0].end").value(end.toString()))
                .andExpect(jsonPath("$[0].item.id").value(firstBooking.getItemId()))
                .andExpect(jsonPath("$[0].item.name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].booker.id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].start").value(start.toString()))
                .andExpect(jsonPath("$[1].end").value(end.toString()))
                .andExpect(jsonPath("$[1].item.id").value(secondBooking.getItemId()))
                .andExpect(jsonPath("$[1].item.name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].booker.id").value(FIRST_ID));
    }

}