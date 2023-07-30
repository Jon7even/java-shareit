package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectParameterException;

import java.util.Optional;

import static ru.practicum.shareit.config.StaticConfig.DEFAULT_COUNT_SIZE;
import static ru.practicum.shareit.constants.NamesParametersInController.X_HEADER_USER_ID;

@UtilityClass
@Slf4j
public class CommonValidator {

    public Long checkParameterUserId(Optional<Long> idUser) {
        if (idUser.isPresent()) {
            if (idUser.get() > 0) {
                log.debug("Checking Header[param={}] [idUser={}] is ok", X_HEADER_USER_ID, idUser.get());
            }
        } else {
            throw new IncorrectParameterException(X_HEADER_USER_ID);
        }

        return idUser.get();
    }

    public Long checkParameterItemRequestId(Optional<Long> requestId) {
        if (requestId.isPresent()) {
            if (requestId.get() > 0) {
                log.debug("Checking [requestId={}] is ok", requestId.get());
            }
        } else {
            throw new IncorrectParameterException("requestId");
        }

        return requestId.get();
    }

    public Long checkParameterBookingId(Optional<Long> idBooking) {
        if (idBooking.isPresent()) {
            if (idBooking.get() > 0) {
                log.debug("Checking Path [idBooking={}] is ok", idBooking.get());
            }
        } else {
            throw new EntityNotFoundException(String.format("Booking with [idBooking=%d]", idBooking.get()));
        }

        return idBooking.get();
    }

    public Long checkParameterItemId(Optional<Long> idItem) {
        if (idItem.isPresent()) {
            if (idItem.get() > 0) {
                log.debug("Checking Path [idItem={}] is ok", idItem.get());
            }
        } else {
            throw new EntityNotFoundException(String.format("Item with [idItem=%d]", idItem.get()));
        }

        return idItem.get();
    }

    public PageRequest getPageRequest(Optional<Integer> from, Optional<Integer> size, Optional<Sort> sort) {
        boolean isExistParamOfSize = from.isPresent();
        boolean isExistParamOfPage = size.isPresent();
        boolean isExistParamSort = sort.isPresent();

        int pageResponse = 0;
        int sizeResponse = DEFAULT_COUNT_SIZE;

        if (isExistParamOfSize && isExistParamOfPage) {
            int pageFrom = from.get();
            int sizeFrom = size.get();

            if (pageFrom >= 0 && sizeFrom >= 1) {
                pageResponse = pageFrom / sizeFrom;
                sizeResponse = sizeFrom;
            } else {
                log.warn("User used incorrect parameters: [from={}] and [size={}]", from, size);
                throw new IncorrectParameterException("from and size");
            }
        }

        if (isExistParamSort) {
            return PageRequest.of(pageResponse, sizeResponse, sort.get());
        } else {
            return PageRequest.of(pageResponse, sizeResponse);
        }
    }

}
