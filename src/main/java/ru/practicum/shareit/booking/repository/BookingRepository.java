package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByIdDesc(Long userId);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.status = ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findAllByUserIdAndStatus(User user, BookingStatus status);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.start <= ?2 " +
            "  AND bk.end >= ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findCurrentByUserId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.end < ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findAllPastByUserId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.start > ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findFutureByUserId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findAllItemsBookingByOwnerId(User user);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.status = ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findItemsBookingByOwnerIdAndStatus(User user, BookingStatus status);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.start <= ?2 " +
            "  AND bk.end >= ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findCurrentItemsBookingByOwnerId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.end < ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findPastItemsBookingByOwnerId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.start > ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<Booking> findFutureItemsBookingByOwnerId(User user, LocalDateTime currentTime);

    @Query("SELECT bk " +
            " FROM Booking AS bk " +
            " JOIN FETCH bk.item it" +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND it = ?2 " +
            "  AND bk.end < ?3 " +
            "ORDER BY bk.start " +
            " DESC ")
    List<Booking> getBookingByOwnerBeforeCurrentTime(User user, Item item, LocalDateTime currentTime);

    List<Booking> findByItemOrderByStart(Item item);
}