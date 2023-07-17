package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingRequestCreateDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_STATUS_AFTER_CREATED;
import static ru.practicum.shareit.constants.NamesLogsInService.*;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingDao repositoryBooking;
    private final ItemDao repositoryItem;
    private final UserDao repositoryUser;

    @Override
    public BookingResponseDTO createBooking(BookingRequestCreateDTO bookingRequestCreateDTO, Optional<Long> idUser) {
        log.debug("New booking came {} [BookingRequestCreateDTO={}]", SERVICE_FROM_CONTROLLER, bookingRequestCreateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        checkStartAndEndTime(bookingRequestCreateDTO, checkedUserId);

        Booking BookingForCreateInRepository = validBookingForCreate(bookingRequestCreateDTO, checkedUserId);

        log.debug("Add new [booking={}] {}", BookingForCreateInRepository, SERVICE_IN_DB);
        Booking createdBooking = repositoryBooking.save(BookingForCreateInRepository);
        Optional<Booking> foundBookingAfterCreation = repositoryBooking.findById(createdBooking.getId());

        if (foundBookingAfterCreation.isPresent() && createdBooking.equals(foundBookingAfterCreation.get())) {
            log.debug("New booking has returned [booking={}] {}", createdBooking, SERVICE_FROM_DB);
            return BookingMapper.INSTANCE.toDTOResponseFromEntity(createdBooking);
        } else {
            log.error("[booking={}] was not created", createdBooking);
            throw new EntityNotCreatedException("New booking");
        }
    }

    @Override
    public BookingResponseDTO findBookingById(Optional<Long> idUser, Optional<Long> idBooking) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedBookingId = checkParameterBookingId(idBooking);
        findUserEntityById(checkedUserId);

        log.debug("Get booking by [idBooking={}] by User [idUser={}] {}", idBooking, checkedUserId, SERVICE_IN_DB);
        Optional<Booking> foundBookingById = repositoryBooking.findById(checkedBookingId);

        if (foundBookingById.isPresent()) {
            log.debug("Found [booking={}] {}", foundBookingById.get(), SERVICE_FROM_DB);
            return BookingMapper.INSTANCE.toDTOResponseFromEntity(foundBookingById.get());
        } else {
            log.warn("Booking by [idBooking={}] by User [idUser={}] was not found", idBooking, checkedUserId);
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", idBooking));
        }
    }

    @Override
    public BookingResponseDTO confirmBooking(Optional<Long> idUser, Optional<Long> idBooking, Optional<Boolean> approved) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedBookingId = checkParameterBookingId(idBooking);
        BookingStatus statusForUpdate = getStatusForUpdate(approved);

        Booking BookingForUpdateStatus = validBookingForUpdateStatus(checkedUserId, checkedBookingId, statusForUpdate);

        log.debug("Update Booking status[status={}] for [bookingId={}] by [bookerId={}] {}", statusForUpdate,
                checkedBookingId, checkedUserId, SERVICE_IN_DB);
        Booking updatedBooking = repositoryBooking.save(BookingForUpdateStatus);

        if (updatedBooking.getStatus().equals(statusForUpdate)) {
            log.debug("Owner [IdUser={}] by Item[IdItem={}] success updated status [bookingId={}] {}", checkedUserId,
                    updatedBooking.getItem().getId(), checkedBookingId, SERVICE_FROM_DB);
            return BookingMapper.INSTANCE.toDTOResponseFromEntity(updatedBooking);
        } else {
            log.debug("Owner [IdUser={}] by Item[IdItem={}] did not update status [bookingId={}] {}", checkedUserId,
                    updatedBooking.getItem().getId(), checkedBookingId, SERVICE_FROM_DB);
            throw new EntityNotUpdatedException("Status booking");
        }

    }

    private Booking validBookingForCreate(BookingRequestCreateDTO bookingRequestCreateDTO, Long checkedUserId) {
        User checkedUserFromDB = findUserEntityById(checkedUserId);
        Item checkedItemFromDB = findItemEntityById(bookingRequestCreateDTO.getItemId());

        if (!checkedItemFromDB.isAvailable()) {
            log.debug("Booking item [item{}] by [booker_id={}] was suspended", checkedItemFromDB, checkedUserFromDB);
            throw new EntityAlreadyBookedException(String.format("Item with [idItem=%d]", checkedItemFromDB.getId()));
        }

        return BookingMapper.INSTANCE.toEntityFromDTOCreate(bookingRequestCreateDTO,
                checkedItemFromDB, checkedUserFromDB, DEFAULT_STATUS_AFTER_CREATED);
    }

    private Booking validBookingForUpdateStatus(Long checkedUserId, Long checkedBookingId,
                                                BookingStatus statusForUpdate) {
        User checkedUserFromDB = findUserEntityById(checkedUserId);
        Booking checkedBookingFromDB = findBookingEntityById(checkedBookingId);

        if (!checkedBookingFromDB.getItem().getUser().getId().equals(checkedUserFromDB.getId())) {
            log.error("User by [idUser={}] is not owner of item[IdItem={}]",
                    checkedUserId, checkedBookingFromDB.getItem().getId());
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", checkedBookingId));
        }

        if (checkedBookingFromDB.getStatus().equals(BookingStatus.APPROVED) ||
                checkedBookingFromDB.getStatus().equals(BookingStatus.REJECTED)) {
            log.debug("Booking status for [idBooking={}] already exposed", checkedBookingId);
            throw new IncorrectParameterException(statusForUpdate.toString());
        }

        checkedBookingFromDB.setStatus(statusForUpdate);

        return checkedBookingFromDB;
    }

    private Booking findBookingEntityById(Long checkedBookingId) {
        log.debug("Get booking entity for checking by [idBooking={}] {}", checkedBookingId, SERVICE_IN_DB);
        Optional<Booking> foundCheckBooking = repositoryBooking.findById(checkedBookingId);

        if (foundCheckBooking.isPresent()) {
            log.debug("Check was successful found [booking={}] {}", foundCheckBooking.get(), SERVICE_FROM_DB);
            return foundCheckBooking.get();
        } else {
            log.warn("Booking by [idBooking={}] was not found", foundCheckBooking);
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", checkedBookingId));
        }
    }

    private User findUserEntityById(Long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<User> foundCheckUser = repositoryUser.findById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private Item findItemEntityById(Long checkedItemId) {
        log.debug("Get item entity for checking by [idItem={}] {}", checkedItemId, SERVICE_IN_DB);
        Optional<Item> foundCheckItem = repositoryItem.findById(checkedItemId);

        if (foundCheckItem.isPresent()) {
            log.debug("Check was successful found [item={}] {}", foundCheckItem.get(), SERVICE_FROM_DB);
            return foundCheckItem.get();
        } else {
            log.warn("Item by [idItem={}] was not found", checkedItemId);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    private void checkStartAndEndTime(BookingRequestCreateDTO bookingRequestCreateDTO, Long checkedUserId) {
        LocalDateTime startTime = bookingRequestCreateDTO.getStart();
        LocalDateTime endTime = bookingRequestCreateDTO.getEnd();

        if (startTime.equals(endTime)) {
            log.warn("User [idUser={}] return equals parameters [Start time] and [End time]", checkedUserId);
            throw new ValidationException(Collections.singleton(Map.of("start and end time",
                    String.format("Time booking [%s] and [%s] is equals", startTime, endTime))));
        }

        if (endTime.isBefore(startTime)) {
            log.warn("User [idUser={}] return [End time] more [Start time]", checkedUserId);
            throw new ValidationException(Collections.singleton(Map.of("endTime",
                    String.format("Time booking [%s] more [%s]", endTime, startTime))));
        }
    }

    private Long checkParameterBookingId(Optional<Long> idBooking) {
        if (idBooking.isPresent()) {
            if (idBooking.get() > 0) {
                log.debug("Checking Path [idBooking={}] is ok", idBooking.get());
            }
        } else {
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", idBooking.get()));
        }

        return idBooking.get();
    }

    private Boolean checkParameterApproved(Optional<Boolean> approved) {
        if (approved.isPresent()) {
            log.debug("Checking RequestParam [approved={}] is ok", approved.get());
            return approved.get();
        } else {
            throw new IncorrectParameterException("approved");
        }
    }

    private Long checkParameterUserId(Optional<Long> idUser) {
        if (idUser.isPresent()) {
            if (idUser.get() > 0) {
                log.debug("Checking Header[param={}] [idUser={}] is ok", X_HEADER_USER_ID, idUser.get());
            }
        } else {
            throw new IncorrectParameterException(X_HEADER_USER_ID);
        }

        return idUser.get();
    }

    private BookingStatus getStatusForUpdate(Optional<Boolean> approved) {
        Boolean checkedIsApproved = checkParameterApproved(approved);

        if (checkedIsApproved) {
            return BookingStatus.APPROVED;
        } else {
            return BookingStatus.REJECTED;
        }
    }
}
