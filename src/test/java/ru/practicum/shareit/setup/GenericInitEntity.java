package ru.practicum.shareit.setup;

import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentResponseTO;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public class GenericInitEntity {
    protected Long id;

    protected LocalDateTime currentTime;

    protected UserEntity userEntity;

    protected ItemEntity itemEntity;

    protected ItemRequestEntity itemRequestEntity;

    protected Optional<Long> idUserOptional;

    protected Optional<Long> idItemOptional;


    protected Optional<Long> idRequestOptional;

    protected Optional<Integer> fromOptional;

    protected Optional<Integer> sizeOptional;

    protected Optional<String> textOptional;

    protected CommentResponseTO commentDTO;

    protected CommentEntity commentEntity;

    protected BookingEntity bookingEntity;

    protected BookingState state;

    protected void initOptionalVariable() {
        idUserOptional = Optional.of(1L);
        idItemOptional = Optional.of(1L);
        idRequestOptional = Optional.of(1L);
        fromOptional = Optional.of(0);
        sizeOptional = Optional.of(20);
        textOptional = Optional.of("TextSearch");
        state = BookingState.ALL;
    }

    protected void initTestVariable(boolean initItem, boolean initBooking, boolean initItemRequest) {
        id = 1L;
        currentTime = LocalDateTime.now();
        userEntity = UserEntity.builder().id(1L).name("testUserName").email("test@ya.ru").build();
        if (initItem) {
            initItemAndComment();
        }
        if (initBooking) {
            initBooking();
        }
        if (initItemRequest) {
            initItemRequest();
        }
    }

    private void initBooking() {
        bookingEntity = BookingEntity.builder()
                .id(id)
                .start(currentTime)
                .end(currentTime.plusHours(1))
                .item(itemEntity)
                .user(userEntity)
                .status(BookingStatus.APPROVED)
                .build();
    }

    private void initItemAndComment() {
        itemEntity = ItemEntity.builder()
                .id(id)
                .name("testItemName")
                .description("testDescriptionItem")
                .available(true)
                .user(userEntity)
                .build();

        commentDTO = CommentResponseTO.builder()
                .id(id)
                .text("textDTO")
                .authorName("testAuthorName")
                .created(currentTime)
                .build();

        commentEntity = CommentEntity.builder()
                .id(id)
                .text("textEntity")
                .item(itemEntity)
                .user(userEntity)
                .created(currentTime)
                .build();
    }

    private void initItemRequest() {
        currentTime = LocalDateTime.now();
        itemRequestEntity = ItemRequestEntity.builder()
                .id(id)
                .description("testDescriptionItemRequest")
                .requestor(userEntity)
                .created(currentTime)
                .build();
    }

}
