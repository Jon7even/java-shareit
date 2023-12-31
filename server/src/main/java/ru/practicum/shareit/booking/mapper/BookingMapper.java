package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingQueueTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "bookingRequestCreateDTO.start", target = "start")
    @Mapping(source = "bookingRequestCreateDTO.end", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "bookingStatus", target = "status")
    BookingEntity toEntityFromDTOCreate(BookingCreateTO bookingRequestCreateDTO,
                                        ItemEntity item,
                                        UserEntity user,
                                        BookingStatus bookingStatus);

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "booking.start", target = "start")
    @Mapping(source = "booking.end", target = "end")
    @Mapping(source = "booking.user.id", target = "booker.id")
    @Mapping(source = "booking.item.id", target = "item.id")
    @Mapping(source = "booking.item.name", target = "item.name")
    @Mapping(source = "booking.status", target = "status")
    BookingResponseTO toDTOResponseFromEntity(BookingEntity booking);

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "booking.user.id", target = "bookerId")
    BookingQueueTO toDTOResponseShortFromEntity(BookingEntity booking);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "from", target = "from")
    @Mapping(source = "size", target = "size")
    BookingRequestListTO toDTOFromRequestParam(Long idUser,
                                               BookingState state,
                                               Optional<Integer> from,
                                               Optional<Integer> size);
}
