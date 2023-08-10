package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findAllByUserIdBooking(UserEntity user, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.status = ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findAllByUserIdAndStatus(UserEntity user, BookingStatus status, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.start <= ?2 " +
            "  AND bk.end >= ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findCurrentByUserId(UserEntity user, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.end < ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findAllPastByUserId(UserEntity user, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item " +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND bk.start > ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findFutureByUserId(UserEntity user, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findAllItemsBookingByOwnerId(UserEntity user, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.status = ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findItemsBookingByOwnerIdAndStatus(UserEntity user, BookingStatus status, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.start <= ?2 " +
            "  AND bk.end >= ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findCurrentItemsBookingByOwnerId(UserEntity user, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.end < ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findPastItemsBookingByOwnerId(UserEntity user, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item AS it " +
            " JOIN FETCH bk.user " +
            "WHERE it.user = ?1 " +
            "  AND bk.start > ?2 " +
            "ORDER BY bk.start " +
            " DESC")
    List<BookingEntity> findFutureItemsBookingByOwnerId(UserEntity user, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT bk " +
            " FROM BookingEntity AS bk " +
            " JOIN FETCH bk.item it" +
            " JOIN FETCH bk.user " +
            "WHERE bk.user = ?1 " +
            "  AND it = ?2 " +
            "  AND bk.end < ?3 " +
            "ORDER BY bk.start " +
            " DESC ")
    List<BookingEntity> getBookingByOwnerBeforeCurrentTime(UserEntity user, ItemEntity item, LocalDateTime currentTime);

    List<BookingEntity> findByItemOrderByStart(ItemEntity item);
}