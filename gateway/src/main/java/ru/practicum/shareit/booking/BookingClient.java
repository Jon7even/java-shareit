package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateTO;
import ru.practicum.shareit.booking.dto.BookingRequestListTO;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

import static ru.practicum.shareit.constants.NamesInController.API_BOOKINGS;

@Service
public class BookingClient extends BaseClient {
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_BOOKINGS))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreateTO bookingCreateTO) {
        return post("", userId, bookingCreateTO);
    }

    public ResponseEntity<Object> findBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> confirmBooking(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getListBookingByIdUser(BookingRequestListTO bookingRequestListTO) {
        Map<String, Object> parameters = Map.of(
                "state", bookingRequestListTO.getState(),
                "from", bookingRequestListTO.getFrom(),
                "size", bookingRequestListTO.getSize()
        );
        return get("?state={state}&from={from}&size={size}", bookingRequestListTO.getIdUser(), parameters);
    }

    public ResponseEntity<Object> getAllItemBookingByIdOwner(BookingRequestListTO bookingRequestListTO) {
        Map<String, Object> parameters = Map.of(
                "state", bookingRequestListTO.getState(),
                "from", bookingRequestListTO.getFrom(),
                "size", bookingRequestListTO.getSize()
        );
        return get("/owner?state={state}&from={from}&size={size}", bookingRequestListTO.getIdUser(), parameters);
    }

}
