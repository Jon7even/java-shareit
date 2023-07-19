package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface BookingDao extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByIdDesc(Long userId);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item " +
            " JOIN bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.status = ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findAllByUserIdAndStatus(User user, BookingStatus status);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item " +
            " JOIN bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.start <= ?2 " +
            "  AND bk.end >= ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findCurrentByUserId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item " +
            " JOIN bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.end < ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findAllPastByUserId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item " +
            " JOIN bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.start > ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findFutureByUserId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item AS it" +
            " JOIN bk.user " +
            "WHERE it.user = ?1 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findAllItemsBookingByOwnerId(User user);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item AS it" +
            " JOIN bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.status = ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findItemsBookingByOwnerIdAndStatus(User user, BookingStatus status);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item AS it" +
            " JOIN bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.start <= ?2 " +
            "  AND bk.end >= ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findCurrentItemsBookingByOwnerId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item AS it" +
            " JOIN bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.end < ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findPastItemsBookingByOwnerId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN bk.item AS it" +
            " JOIN bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.start > ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findFutureItemsBookingByOwnerId(User user, LocalDateTime currentTime);

}