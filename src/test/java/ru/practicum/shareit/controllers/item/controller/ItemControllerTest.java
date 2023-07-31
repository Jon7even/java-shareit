package ru.practicum.shareit.controllers.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.shareit.controllers.setup.controller.GenericControllerTest;
import ru.practicum.shareit.item.dto.CommentCreateTO;
import ru.practicum.shareit.item.dto.ItemCreateTO;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
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

public class ItemControllerTest extends GenericControllerTest {
    @BeforeEach
    void setUp() {
        initItems();
        initUsers();
        userService.createUser(firstUser);
    }

    @Test
    @DisplayName("Итем должен создаться с релевантными полями без [requestId] [createItem]")
    void shouldCreateItem_withoutRequestId_thenStatus201() throws Exception {
        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));
    }

    @Test
    @DisplayName("Итем должен создаться с релевантными полями c [requestId] [createItem]")
    void shouldCreateItem_withRequestId_thenStatus201() throws Exception {
        firstItem.setRequestId(FIRST_ID);
        initItemRequests();

        mockMvc.perform(post("/requests")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        firstItem.setRequestId(FIRST_ID);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true))
                .andExpect(jsonPath("requestId").value(FIRST_ID));
    }

    @Test
    @DisplayName("Итем не должен создаться [createItem]")
    void shouldNotCreateItem_thenStatus400And404() throws Exception {
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        secondItem.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        secondItem.setAvailable(true);
        secondItem.setName(null);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));

        secondItem.setAvailable(true);
        secondItem.setName("name");
        secondItem.setDescription(null);

        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));
    }

    @Test
    @DisplayName("Поиск итема по [ID] [findItemById]")
    void shouldGetItemById_thenStatus200AndStatus404AndStatus400() throws Exception {
        Long idUser = 1L;
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID + 1)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_ITEM_NOT_FOUND));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_USER_NOT_FOUND));

        mockMvc.perform(get("/items/{itemId}", FIRST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));
    }

    @Test
    @DisplayName("Итем должен обновить поля частично [updateItemById]")
    void shouldItemUseMethodPatch_thenStatus200() throws Exception {
        Long idUser = 1L;
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        firstItem.setName("firstItemAfterUpdate");

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));

        firstItem.setDescription("description_1_AfterUpdate");

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(jsonPath("description").value("description_1_AfterUpdate"))
                .andExpect(jsonPath("available").value(true));

        firstItem.setAvailable(false);

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItemAfterUpdate"))
                .andExpect(jsonPath("description").value("description_1_AfterUpdate"))
                .andExpect(jsonPath("available").value(false));

        firstItem.setName("firstItem");
        firstItem.setDescription("description_1");
        firstItem.setAvailable(true);

        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("firstItem"))
                .andExpect(jsonPath("description").value("description_1"))
                .andExpect(jsonPath("available").value(true));
    }

    @Test
    @DisplayName("Получить все итемы пользователя по [" + X_HEADER_USER_ID + "] default Page [getAllItemsByUserId]")
    void shouldGetAllItemsByIdUser_withoutParameterPageable_thenStatus200() throws Exception {
        userService.createUser(secondUser);

        itemService.createItem(firstItem, Optional.of(1L));
        itemService.createItem(secondItem, Optional.of(2L));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].name").value(secondItem.getName()));
    }

    @Test
    @DisplayName("Получить все итемы пользователя по [" + X_HEADER_USER_ID + "] with Page [getAllItemsByUserId]")
    void shouldGetAllItemsByIdUser_withParameterPageable_thenStatus200() throws Exception {
        userService.createUser(secondUser);

        itemService.createItem(firstItem, Optional.of(1L));
        itemService.createItem(secondItem, Optional.of(2L));

        ItemCreateTO thirdItemDTO = ItemCreateTO.builder()
                .name("thirdItem")
                .description("description_3")
                .available(true)
                .build();
        itemService.createItem(thirdItemDTO, Optional.of(2L));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(2)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].name").value(thirdItemDTO.getName()));

        mockMvc.perform(get("/items?from={from}&size={size}", 2, 2)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(0)))
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/items?from={from}&size={size}", 0, 1)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[0].name").value(secondItem.getName()));

        mockMvc.perform(get("/items?from={from}&size={size}", 1, 1)
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value(thirdItemDTO.getName()));
    }

    @Test
    @DisplayName("Получить все итемы пользователя по [" + X_HEADER_USER_ID + "] c комментариями и бронированиями" +
            " default Page [getAllItemsByUserId]")
    void shouldGetAllItemsByIdUser_withoutParameterPageable_withCommentAndBooking_thenStatus200() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now().minusMinutes(1);
        initBookings();
        userService.createUser(secondUser);

        Long idUser1 = 1L;
        Long idUser2 = 2L;
        itemService.createItem(firstItem, Optional.of(idUser2));
        itemService.createItem(secondItem, Optional.of(idUser2));

        firstBooking.setStart(currentTime);
        firstBooking.setEnd(currentTime.plusSeconds(1));

        secondBooking.setStart(currentTime);
        secondBooking.setEnd(currentTime.plusHours(1));

        bookingService.createBooking(firstBooking, Optional.of(idUser1));
        bookingService.createBooking(secondBooking, Optional.of(idUser1));

        CommentCreateTO firstComment = CommentCreateTO.builder().text("firstComment").build();

        mockMvc.perform(post("/items/{id}/comment", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("text").value(firstComment.getText()))
                .andExpect(jsonPath("authorName").value(firstUser.getName()))
                .andExpect(jsonPath("created").value(notNullValue()));

        mockMvc.perform(get("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID + 1))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(2)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()))
                .andExpect(jsonPath("$[0].description").value(firstItem.getDescription()))
                .andExpect(jsonPath("$[0].available").value(firstItem.getAvailable()))
                .andExpect(jsonPath("$[0].comments[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[0].comments[0].text").value(firstComment.getText()))
                .andExpect(jsonPath("$[0].comments[0].authorName").value(firstUser.getName()))
                .andExpect(jsonPath("$[0].comments[0].created").value(notNullValue()))
                .andExpect(jsonPath("$[1].id").value(FIRST_ID + 1))
                .andExpect(jsonPath("$[1].name").value(secondItem.getName()))
                .andExpect(jsonPath("$[1].description").value(secondItem.getDescription()))
                .andExpect(jsonPath("$[1].available").value(secondItem.getAvailable()))
                .andExpect(jsonPath("$[1].comments[1]").doesNotExist());
    }

    @Test
    @DisplayName("Получить итемы по строке поиска [getListSearchItem]")
    void shouldGetSearchItemsByRequest_thenStatus200() throws Exception {
        Long idUser = 1L;
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(get("/items/search?text={text}", "Item")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(firstItem.getName()));

        itemService.createItem(secondItem, Optional.of(idUser));
        mockMvc.perform(get("/items/search?text={text}", "description_2")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        secondItem.setAvailable(true);
        mockMvc.perform(patch("/items/{itemId}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(secondItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/items/search?text={text}", "description_2")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(secondItem.getName()));

        thirdItem.setName("thirdItem");
        thirdItem.setDescription("thirdItem");
        thirdItem.setAvailable(true);
        mockMvc.perform(post("/items")
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(thirdItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/items/search?text={text}", "rdIt")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("thirdItem"));

        mockMvc.perform(get("/items/search?text={text}", "")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", " ")
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/search?text={text}", "thirdItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(get("/items/search?text={text}&from={from}&size={size}", "Item", 1, 1)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(1)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(3));

        mockMvc.perform(get("/items/search?text={text}&from={from}&size={size}", "Item", 0, 2)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(X_COUNT_ITEMS, String.valueOf(2)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(FIRST_ID))
                .andExpect(jsonPath("$[1].id").value(3));
    }

    @Test
    @DisplayName("Итем должен удалиться по [ID] [deleteItemById]")
    void shouldDeleteItemById_thenStatus204And404() throws Exception {
        Long idUser = 1L;
        itemService.createItem(firstItem, Optional.of(idUser));

        mockMvc.perform(delete("/items/{id}", FIRST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(delete("/items/{id}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/items/{id}", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Комментарий должен создаться с релевантными полями [createComment]")
    void shouldCreateComment_thenStatus200() throws Exception {
        initBookings();
        Long idUser1 = 1L;
        Long idUser2 = 2L;
        userService.createUser(secondUser);
        itemService.createItem(firstItem, Optional.of(idUser2));
        LocalDateTime currentTime = LocalDateTime.now().minusMinutes(1);
        firstBooking.setStart(currentTime);
        firstBooking.setEnd(currentTime.plusSeconds(1));
        bookingService.createBooking(firstBooking, Optional.of(idUser1));

        CommentCreateTO firstComment = CommentCreateTO.builder().text("firstComment").build();

        mockMvc.perform(post("/items/{id}/comment", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("text").value(firstComment.getText()))
                .andExpect(jsonPath("authorName").value(firstUser.getName()))
                .andExpect(jsonPath("created").value(notNullValue()));
    }

    @Test
    @DisplayName("Комментарий не должен создаться [createComment]")
    void shouldNotCreateComment_thenStatus400And404() throws Exception {
        Long idUser = 1L;
        itemService.createItem(firstItem, Optional.of(idUser));
        CommentCreateTO firstComment = CommentCreateTO.builder().text("firstComment").build();

        String errorNotBookingForThisItem = "User with [idUser=1] not have completed bookings for this item[idItem=1]";

        mockMvc.perform(post("/items/{id}/comment", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(errorNotBookingForThisItem));

        mockMvc.perform(post("/items/{id}/comment", FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_HEADER_USER_ID));

        mockMvc.perform(post("/items/{id}/comment", FIRST_ID + 1)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_ITEM_NOT_FOUND));

        firstComment.setText(null);

        mockMvc.perform(post("/items/{id}/comment", FIRST_ID)
                        .header(X_HEADER_USER_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_NAME).value(ERROR_M_VALIDATION));
    }

}
