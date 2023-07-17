package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingRequestCreateDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "bookingRequestCreateDTO.start", target = "start")
    @Mapping(source = "bookingRequestCreateDTO.end", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "bookingStatus", target = "status")
    Booking toEntityFromDTOCreate(BookingRequestCreateDTO bookingRequestCreateDTO,
                                  Item item, User user, BookingStatus bookingStatus);

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "booking.start", target = "start")
    @Mapping(source = "booking.end", target = "end")
    @Mapping(source = "booking.user.id", target = "booker.id")
    @Mapping(source = "booking.item.id", target = "item.id")
    @Mapping(source = "booking.item.name", target = "item.name")
    @Mapping(source = "booking.status", target = "status")
    BookingResponseDTO toDTOResponseFromEntity(Booking booking);
}
