package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.booking.dto.BookingResponseTO;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.CommonValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.config.StaticConfig.*;
import static ru.practicum.shareit.constants.NamesLogsInService.*;

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
    public BookingResponseTO createBooking(BookingCreateTO bookingRequestCreateTO, Optional<Long> idUser) {
        log.debug("New bookingTO came {} [BookingRequestCreateTO={}]", SERVICE_FROM_CONTROLLER, bookingRequestCreateTO);
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        checkStartAndEndTime(bookingRequestCreateTO, checkedUserId);

        BookingEntity bookingForCreateInRepository = validBookingForCreate(bookingRequestCreateTO, checkedUserId);
        log.debug("Add new entity [booking={}] {}", bookingForCreateInRepository, SERVICE_IN_DB);

        BookingEntity createdBooking = repositoryBooking.save(bookingForCreateInRepository);
        log.debug("New booking has returned [booking={}] {}", createdBooking, SERVICE_FROM_DB);

        return BookingMapper.INSTANCE.toDTOResponseFromEntity(createdBooking);
    }

    @Override
    public BookingResponseTO findBookingById(Optional<Long> idUser, Optional<Long> idBooking) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedBookingId = CommonValidator.checkParameterBookingId(idBooking);
        existDoesUserEntityById(checkedUserId);

        log.debug("Get booking by [idBooking={}] by User [idUser={}] {}",
                checkedBookingId, checkedUserId, SERVICE_IN_DB);
        Optional<BookingEntity> foundBookingById = repositoryBooking.findById(checkedBookingId);

        if (foundBookingById.isPresent()) {
            boolean userEqualsOwnerItem = foundBookingById.get().getItem().getUser().getId().equals(checkedUserId);
            boolean userEqualsOwnerBooking = foundBookingById.get().getUser().getId().equals(checkedUserId);

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
    public BookingResponseTO confirmBooking(Optional<Long> idUser,
                                            Optional<Long> idBooking,
                                            Optional<Boolean> approved) {
        Long checkedUserId = CommonValidator.checkParameterUserId(idUser);
        Long checkedBookingId = CommonValidator.checkParameterBookingId(idBooking);
        BookingStatus statusForUpdate = getStatusForUpdate(approved);

        BookingEntity bookingForUpdateStatus = validBookingForUpdateStatus(
                checkedUserId, checkedBookingId, statusForUpdate
        );
        log.debug("Update Booking status[status={}] for [bookingId={}] by [bookerId={}] {}", statusForUpdate,
                checkedBookingId, checkedUserId, SERVICE_IN_DB);

        BookingEntity updatedBooking = repositoryBooking.save(bookingForUpdateStatus);
        log.debug("Owner [IdUser={}] by Item[IdItem={}] success updated status [bookingId={}] {}", checkedUserId,
                updatedBooking.getItem().getId(), checkedBookingId, SERVICE_FROM_DB);

        return BookingMapper.INSTANCE.toDTOResponseFromEntity(updatedBooking);
    }

    @Override
    public List<BookingResponseTO> getListBookingByIdUser(BookingRequestListTO bookingRequestListTO) {
        Long validUserId = bookingRequestListTO.getIdUser();
        UserEntity checkedUserFromDB = findUserEntityById(validUserId);

        LocalDateTime currentTime = LocalDateTime.now();
        List<BookingEntity> bookings = Collections.emptyList();
        Pageable page = CommonValidator.getPageRequest(
                bookingRequestListTO.getFrom(), bookingRequestListTO.getSize(), Optional.empty()
        );

        log.debug("Get list booking by user [userId={}] [BookingState={}], [page={}] [currentTime={}] {}",
                validUserId, bookingRequestListTO.getState(), page, currentTime, SERVICE_FROM_DB);

        switch (bookingRequestListTO.getState()) {
            case ALL:
                bookings = repositoryBooking.findAllByUserIdBooking(checkedUserFromDB, page);
                break;
            case CURRENT:
                bookings = repositoryBooking.findCurrentByUserId(checkedUserFromDB, currentTime, page);
                break;
            case PAST:
                bookings = repositoryBooking.findAllPastByUserId(checkedUserFromDB, currentTime, page);
                break;
            case FUTURE:
                bookings = repositoryBooking.findFutureByUserId(checkedUserFromDB, currentTime, page);
                break;
            case WAITING:
                bookings = repositoryBooking.findAllByUserIdAndStatus(checkedUserFromDB, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = repositoryBooking.findAllByUserIdAndStatus(checkedUserFromDB, BookingStatus.REJECTED, page);
                break;
            default:
                log.error("Unknown detection error of [BookingState={}]", bookingRequestListTO.getState());
                break;
        }

        return bookings.stream()
                .map(BookingMapper.INSTANCE::toDTOResponseFromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseTO> getAllItemBookingByIdOwner(BookingRequestListTO bookingRequestListTO) {
        Long validUserId = bookingRequestListTO.getIdUser();
        UserEntity checkedUserFromDB = findUserEntityById(validUserId);

        LocalDateTime currentTime = LocalDateTime.now();
        List<BookingEntity> bookings = Collections.emptyList();

        Pageable page = CommonValidator.getPageRequest(
                bookingRequestListTO.getFrom(), bookingRequestListTO.getSize(), Optional.empty()
        );

        log.debug("Get list booking by owner [ownerId={}] [BookingState={}], [page={}] [currentTime={}] {}",
                validUserId, bookingRequestListTO.getState().toString(), page, currentTime, SERVICE_FROM_DB);

        switch (bookingRequestListTO.getState()) {
            case ALL:
                bookings = repositoryBooking.findAllItemsBookingByOwnerId(checkedUserFromDB, page);
                break;
            case CURRENT:
                bookings = repositoryBooking.findCurrentItemsBookingByOwnerId(checkedUserFromDB, currentTime, page);
                break;
            case PAST:
                bookings = repositoryBooking.findPastItemsBookingByOwnerId(checkedUserFromDB, currentTime, page);
                break;
            case FUTURE:
                bookings = repositoryBooking.findFutureItemsBookingByOwnerId(checkedUserFromDB, currentTime, page);
                break;
            case WAITING:
                bookings = repositoryBooking.findItemsBookingByOwnerIdAndStatus(checkedUserFromDB,
                        BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = repositoryBooking.findItemsBookingByOwnerIdAndStatus(checkedUserFromDB,
                        BookingStatus.REJECTED, page);
                break;
            default:
                log.error("Unknown detection error of [BookingState={}]", bookingRequestListTO.getState());
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
        existDoesUserEntityById(checkedUserId);
        BookingEntity checkedBookingFromDB = findBookingEntityById(checkedBookingId);

        if (!checkedBookingFromDB.getItem().getUser().getId().equals(checkedUserId)) {
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

    private void existDoesUserEntityById(Long checkedUserId) {
        log.debug("Checking entity user by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        boolean existsById = repositoryUser.existsById(checkedUserId);

        if (existsById) {
            log.debug("Check was successful [idUser={}] exist in {}", checkedUserId, SERVICE_IN_DB);
        } else {
            log.warn("User by [idUser={}] was not found", checkedUserId);
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

    private Boolean checkParameterApproved(Optional<Boolean> approved) {
        if (approved.isPresent()) {
            log.debug("Checking RequestParam [approved={}] is ok", approved.get());
            return approved.get();
        } else {
            throw new IncorrectParameterException("approved");
        }
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
