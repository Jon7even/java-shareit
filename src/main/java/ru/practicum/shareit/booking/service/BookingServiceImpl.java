package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_STATUS_AFTER_CREATED;
import static ru.practicum.shareit.constants.NamesLogsInService.*;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repositoryBooking;
    private final ItemRepository repositoryItem;
    private final UserRepository repositoryUser;

    @Transactional
    @Override
    public BookingResponseTO createBooking(BookingCreateTO bookingRequestCreateDTO, Optional<Long> idUser) {
        log.debug("New booking came {} [BookingRequestCreateDTO={}]", SERVICE_FROM_CONTROLLER, bookingRequestCreateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        checkStartAndEndTime(bookingRequestCreateDTO, checkedUserId);

        BookingEntity bookingForCreateInRepository = validBookingForCreate(bookingRequestCreateDTO, checkedUserId);

        log.debug("Add new [booking={}] {}", bookingForCreateInRepository, SERVICE_IN_DB);
        BookingEntity createdBooking = repositoryBooking.save(bookingForCreateInRepository);
        Optional<BookingEntity> foundBookingAfterCreation = repositoryBooking.findById(createdBooking.getId());

        if (foundBookingAfterCreation.isPresent() && createdBooking.equals(foundBookingAfterCreation.get())) {
            log.debug("New booking has returned [booking={}] {}", createdBooking, SERVICE_FROM_DB);
            return BookingMapper.INSTANCE.toDTOResponseFromEntity(createdBooking);
        } else {
            log.error("[booking={}] was not created", createdBooking);
            throw new EntityNotCreatedException("New booking");
        }
    }

    @Override
    public BookingResponseTO findBookingById(Optional<Long> idUser, Optional<Long> idBooking) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedBookingId = checkParameterBookingId(idBooking);
        UserEntity user = findUserEntityById(checkedUserId);

        log.debug("Get booking by [idBooking={}] by User [idUser={}] {}", checkedBookingId, checkedUserId, SERVICE_IN_DB);
        Optional<BookingEntity> foundBookingById = repositoryBooking.findById(checkedBookingId);

        if (foundBookingById.isPresent()) {
            boolean userEqualsOwnerItem = foundBookingById.get().getItem().getUser().equals(user);
            boolean userEqualsOwnerBooking = foundBookingById.get().getUser().equals(user);

            if (userEqualsOwnerItem || userEqualsOwnerBooking) {
                log.debug("Found [booking={}] {}", foundBookingById.get(), SERVICE_FROM_DB);
                return BookingMapper.INSTANCE.toDTOResponseFromEntity(foundBookingById.get());
            } else {
                log.warn("User [idUser={}] not is owner booking [idBooking={}] or owner item [idItem={}]",
                        checkedUserId, checkedBookingId, foundBookingById.get().getItem().getId());
                throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", checkedBookingId));
            }

        } else {
            log.warn("Booking by [idBooking={}] by User [idUser={}] was not found", checkedBookingId, checkedUserId);
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", checkedBookingId));
        }
    }

    @Transactional
    @Override
    public BookingResponseTO confirmBooking(Optional<Long> idUser, Optional<Long> idBooking, Optional<Boolean> approved) {
        Long checkedUserId = checkParameterUserId(idUser);
        Long checkedBookingId = checkParameterBookingId(idBooking);
        BookingStatus statusForUpdate = getStatusForUpdate(approved);

        BookingEntity bookingForUpdateStatus = validBookingForUpdateStatus(checkedUserId, checkedBookingId, statusForUpdate);

        log.debug("Update Booking status[status={}] for [bookingId={}] by [bookerId={}] {}", statusForUpdate,
                checkedBookingId, checkedUserId, SERVICE_IN_DB);
        BookingEntity updatedBooking = repositoryBooking.save(bookingForUpdateStatus);

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

    @Override
    public List<BookingResponseTO> getListBookingByIdUser(Optional<Long> idUser, BookingState state) {
        Long checkedUserId = checkParameterUserId(idUser);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        LocalDateTime currentTime = LocalDateTime.now();

        List<BookingEntity> bookings = Collections.emptyList();
        log.debug("Get list booking by user [userId={}] [BookingState={}], [currentTime={}] {}", checkedUserId,
                state.toString(), currentTime, SERVICE_FROM_DB);

        switch (state) {
            case ALL:
                bookings = repositoryBooking.findByUserIdOrderByIdDesc(checkedUserId);
                break;
            case CURRENT:
                bookings = repositoryBooking.findCurrentByUserId(checkedUserFromDB, currentTime);
                break;
            case PAST:
                bookings = repositoryBooking.findAllPastByUserId(checkedUserFromDB, currentTime);
                break;
            case FUTURE:
                bookings = repositoryBooking.findFutureByUserId(checkedUserFromDB, currentTime);
                break;
            case WAITING:
                bookings = repositoryBooking.findAllByUserIdAndStatus(checkedUserFromDB, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repositoryBooking.findAllByUserIdAndStatus(checkedUserFromDB, BookingStatus.REJECTED);
                break;
            default:
                log.error("Unknown detection error of [BookingState={}]", state);
                break;
        }

        return bookings.stream()
                .map(BookingMapper.INSTANCE::toDTOResponseFromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseTO> getAllItemBookingByIdOwner(Optional<Long> idUser, BookingState state) {
        Long checkedUserId = checkParameterUserId(idUser);
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        LocalDateTime currentTime = LocalDateTime.now();

        List<BookingEntity> bookings = Collections.emptyList();
        log.debug("Get list booking by owner [ownerId={}] [BookingState={}], [currentTime={}] {}", checkedUserId,
                state.toString(), currentTime, SERVICE_FROM_DB);

        switch (state) {
            case ALL:
                bookings = repositoryBooking.findAllItemsBookingByOwnerId(checkedUserFromDB);
                break;
            case CURRENT:
                bookings = repositoryBooking.findCurrentItemsBookingByOwnerId(checkedUserFromDB, currentTime);
                break;
            case PAST:
                bookings = repositoryBooking.findPastItemsBookingByOwnerId(checkedUserFromDB, currentTime);
                break;
            case FUTURE:
                bookings = repositoryBooking.findFutureItemsBookingByOwnerId(checkedUserFromDB, currentTime);
                break;
            case WAITING:
                bookings = repositoryBooking.findItemsBookingByOwnerIdAndStatus(checkedUserFromDB,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repositoryBooking.findItemsBookingByOwnerIdAndStatus(checkedUserFromDB,
                        BookingStatus.REJECTED);
                break;
            default:
                log.error("Unknown detection error of [BookingState={}]", state);
                break;
        }

        return bookings.stream()
                .map(BookingMapper.INSTANCE::toDTOResponseFromEntity)
                .collect(Collectors.toList());
    }

    private BookingEntity validBookingForCreate(BookingCreateTO bookingRequestCreateDTO, Long checkedUserId) {
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        ItemEntity checkedItemFromDB = findItemEntityById(bookingRequestCreateDTO.getItemId());

        if (!checkedItemFromDB.isAvailable()) {
            log.debug("Booking item [item{}] by [booker_id={}] was suspended", checkedItemFromDB, checkedUserFromDB);
            throw new EntityAlreadyBookedException(String.format("Item with [idItem=%d]", checkedItemFromDB.getId()));
        }

        if (checkedItemFromDB.getUser().getId().equals(checkedUserId)) {
            log.debug("User [userId={}] is owner item, booking rejected", checkedUserId);
            throw new EntityNotFoundException("Item where you are not the owner");
        }

        return BookingMapper.INSTANCE.toEntityFromDTOCreate(bookingRequestCreateDTO,
                checkedItemFromDB, checkedUserFromDB, DEFAULT_STATUS_AFTER_CREATED);
    }

    private BookingEntity validBookingForUpdateStatus(Long checkedUserId, Long checkedBookingId,
                                                      BookingStatus statusForUpdate) {
        UserEntity checkedUserFromDB = findUserEntityById(checkedUserId);
        BookingEntity checkedBookingFromDB = findBookingEntityById(checkedBookingId);

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

    private BookingEntity findBookingEntityById(Long checkedBookingId) {
        log.debug("Get booking entity for checking by [idBooking={}] {}", checkedBookingId, SERVICE_IN_DB);
        Optional<BookingEntity> foundCheckBooking = repositoryBooking.findById(checkedBookingId);

        if (foundCheckBooking.isPresent()) {
            log.debug("Check was successful found [booking={}] {}", foundCheckBooking.get(), SERVICE_FROM_DB);
            return foundCheckBooking.get();
        } else {
            log.warn("Booking by [idBooking={}] was not found", foundCheckBooking);
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", checkedBookingId));
        }
    }

    private UserEntity findUserEntityById(Long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<UserEntity> foundCheckUser = repositoryUser.findById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private ItemEntity findItemEntityById(Long checkedItemId) {
        log.debug("Get item entity for checking by [idItem={}] {}", checkedItemId, SERVICE_IN_DB);
        Optional<ItemEntity> foundCheckItem = repositoryItem.findById(checkedItemId);

        if (foundCheckItem.isPresent()) {
            log.debug("Check was successful found [item={}] {}", foundCheckItem.get(), SERVICE_FROM_DB);
            return foundCheckItem.get();
        } else {
            log.warn("Item by [idItem={}] was not found", checkedItemId);
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", checkedItemId));
        }
    }

    private void checkStartAndEndTime(BookingCreateTO bookingRequestCreateDTO, Long checkedUserId) {
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
