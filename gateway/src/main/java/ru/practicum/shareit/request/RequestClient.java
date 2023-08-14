package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;

import java.util.Map;

import static ru.practicum.shareit.constants.NamesInController.API_ITEM_REQUESTS;

@Service
public class RequestClient extends BaseClient {
    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_ITEM_REQUESTS))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(long userId, ItemRequestCreateTO itemRequestCreateTO) {
        return post("", userId, itemRequestCreateTO);
    }

    public ResponseEntity<Object> findItemRequestById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllItemRequestByIdOwner(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getListItemRequestByAnyUser(ItemRequestRequestListTO itemRequestRequestListTO) {
        Map<String, Object> parameters = Map.of(
                "from", itemRequestRequestListTO.getFrom(),
                "size", itemRequestRequestListTO.getSize()
        );
        return get("/all?from={from}&size={size}", itemRequestRequestListTO.getIdUser(), parameters);
    }

}
