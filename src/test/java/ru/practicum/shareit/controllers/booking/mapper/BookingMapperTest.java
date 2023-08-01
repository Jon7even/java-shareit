package ru.practicum.shareit.controllers.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingQueueTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.controllers.setup.GenericMapperTest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.config.StaticConfig.DEFAULT_STATUS_AFTER_CREATED;

public class BookingMapperTest extends GenericMapperTest {
    @Test
    void toEntityFromDTOCreate() {
        initTestVariable(true, true, false);
        LocalDateTime endTime = currentTime.plusHours(2);
        BookingCreateTO originalDTO = BookingCreateTO.builder()
                .itemId(id)
                .start(currentTime)
                .end(endTime)
                .build();
        BookingEntity resultEntity = BookingMapper.INSTANCE.toEntityFromDTOCreate(
                originalDTO, itemEntity, userEntity, DEFAULT_STATUS_AFTER_CREATED
        );

        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertEquals(resultEntity.getStart(), currentTime);
        assertEquals(resultEntity.getEnd(), endTime);
        assertEquals(resultEntity.getItem(), itemEntity);
        assertEquals(resultEntity.getUser(), userEntity);
        assertEquals(resultEntity.getStatus(), DEFAULT_STATUS_AFTER_CREATED);
    }

    @Test
    void toDTOResponseFromEntity() {
        initTestVariable(true, true, false);
        BookingResponseTO resultDTO = BookingMapper.INSTANCE.toDTOResponseFromEntity(bookingEntity);

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), bookingEntity.getId());
        assertEquals(resultDTO.getStart(), bookingEntity.getStart());
        assertEquals(resultDTO.getEnd(), bookingEntity.getEnd());
        assertEquals(resultDTO.getItem().getId(), bookingEntity.getItem().getId());
        assertEquals(resultDTO.getItem().getName(), bookingEntity.getItem().getName());
        assertEquals(resultDTO.getBooker().getId(), bookingEntity.getUser().getId());
        assertEquals(resultDTO.getStatus(), bookingEntity.getStatus());
    }

    @Test
    void toDTOResponseShortFromEntity() {
        initTestVariable(true, true, false);
        BookingQueueTO resultDTO = BookingMapper.INSTANCE.toDTOResponseShortFromEntity(bookingEntity);

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), bookingEntity.getId());
        assertEquals(resultDTO.getBookerId(), bookingEntity.getUser().getId());
    }

    @Test
    void toDTOFromRequestParam() {
        initOptionalVariable();
        BookingRequestListTO resultDTO = BookingMapper.INSTANCE.toDTOFromRequestParam(
                idUserOptional, state, fromOptional, sizeOptional
        );

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getState(), state);
        assertEquals(resultDTO.getIdUser(), idUserOptional);
        assertEquals(resultDTO.getFrom(), fromOptional);
        assertEquals(resultDTO.getSize(), sizeOptional);
    }

}
