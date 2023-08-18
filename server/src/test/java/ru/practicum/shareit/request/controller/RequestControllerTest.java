package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.practicum.shareit.setup.GenericControllerTest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.constants.NamesJsonResponse.ERROR_M_VALIDATION;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

public class RequestControllerTest extends GenericControllerTest {
    @Autowired
    ItemRequestService serviceItemRequest;

    @BeforeEach
    void setUp() {
        initUsers();
        initItemRequests();
        userService.createUser(firstUser);
    }

    @Test
    @DisplayName("Запрос на итем должен создаться с релевантными полями [createItemRequest]")
    void shouldCreateItemRequest_thenStatus201() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("description").value(firstItemRequest.getDescription()))
                .andExpect(jsonPath("created").value(notNullValue()))
                .andExpect(jsonPath("items").value(hasSize(0)));
    }

    @Test
    @DisplayName("Запрос на итем не должен создаться [createItemRequest]")
    void shouldNotCreateItemRequest_thenStatus400And404() throws Exception {
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(firstItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(post("/requests")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        firstItemRequest.setDescription(null);

        mockMvc.perform(post("/requests")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));
    }

    @Test
    @DisplayName("Поиск запроса на итем по [ID] [getItemRequestById]")
    void shouldGetItemRequestById_thenStatus200AndStatus404AndStatus400() throws Exception {
        serviceItemRequest.createItemRequest(firstItemRequest, Optional.of(1L));

        mockMvc.perform(get("/requests/{requestId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("description").value(firstItemRequest.getDescription()))
                .andExpect(jsonPath("created").value(notNullValue()))
                .andExpect(jsonPath("items").value(hasSize(0)));

        initItems();
        firstItem.setRequestId(FIRST_ID);
        itemService.createItem(firstItem, Optional.of(1L));

        mockMvc.perform(get("/requests/{requestId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("description").value(firstItemRequest.getDescription()))
                .andExpect(jsonPath("created").value(notNullValue()))
                .andExpect(jsonPath("items").value(hasSize(1)))
                .andExpect(jsonPath("items[0].id").value(FIRST_ID))
                .andExpect(jsonPath("items[0].name").value(firstItem.getName()));

        String errorItemRequestNotFound = "[ItemRequest with [idItemRequest=2]] not found";

        mockMvc.perform(get("/requests/{requestId}", FIRST_ID + 1)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(errorItemRequestNotFound));

        mockMvc.perform(get("/requests/{requestId}", FIRST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(get("/requests/{requestId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));
    }

    @Test
    @DisplayName("Получить владельцем список своих запросов на итем[getAllItemRequestByIdOwner]")
    void shouldGetListItemRequestByIdOwner_SortDescId_thenStatus200() throws Exception {
        serviceItemRequest.createItemRequest(firstItemRequest, Optional.of(1L));
        serviceItemRequest.createItemRequest(secondItemRequest, Optional.of(1L));

        mockMvc.perform(get("/requests")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].description").value(secondItemRequest.getDescription()))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].description").value(firstItemRequest.getDescription()));
    }

    @Test
    @DisplayName("Получить список запросов на итем любым пользователем[getListItemRequestByAnyUser]")
    void shouldGetListItemRequestByIdAnyUser_withoutAndWithParameterPageable_thenStatus200() throws Exception {
        serviceItemRequest.createItemRequest(firstItemRequest, Optional.of(1L));
        serviceItemRequest.createItemRequest(secondItemRequest, Optional.of(1L));
        userService.createUser(secondUser);
        serviceItemRequest.createItemRequest(thirdItemRequest, Optional.of(2L));

        mockMvc.perform(get("/requests/all")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 2))
                .andExpect(jsonPath("$[0].description").value(thirdItemRequest.getDescription()));

        mockMvc.perform(get("/requests/all")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].description").value(secondItemRequest.getDescription()))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].description").value(firstItemRequest.getDescription()));

        mockMvc.perform(get("/requests/all?from={from}&size={size}", 0, 20)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].description").value(secondItemRequest.getDescription()))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].description").value(firstItemRequest.getDescription()));

        mockMvc.perform(get("/requests/all?from={from}&size={size}", 1, 1)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].description").value(secondItemRequest.getDescription()));
    }
}
