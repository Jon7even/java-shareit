package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.Booking;

@Component
public interface BookingDao extends JpaRepository<Booking, Long> {
}