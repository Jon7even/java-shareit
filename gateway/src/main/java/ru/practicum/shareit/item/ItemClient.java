package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateTO;
import ru.practicum.shareit.item.dto.ItemCreateTO;
import ru.practicum.shareit.item.dto.ItemRequestListTO;
import ru.practicum.shareit.item.dto.ItemUpdateTO;

import java.util.Map;

import static ru.practicum.shareit.constants.NamesInController.API_ITEMS;

@Service
public class ItemClient extends BaseClient {
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_ITEMS))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemCreateTO itemCreateTO) {
        return post("", userId, itemCreateTO);
    }

    public ResponseEntity<Object> findItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateTO itemUpdateTO) {
        return patch("/" + itemId, userId, itemUpdateTO);
    }

    public ResponseEntity<Object> getAllItemsByUserId(ItemRequestListTO itemRequestListTO) {
        Map<String, Object> parameters = Map.of(
                "from", itemRequestListTO.getFrom(),
                "size", itemRequestListTO.getSize()
        );
        return get("?from={from}&size={size}", itemRequestListTO.getIdUser(), parameters);
    }

    public ResponseEntity<Object> getListSearchItem(ItemRequestListTO itemRequestListTO) {
        Map<String, Object> parameters = Map.of(
                "text", itemRequestListTO.getText(),
                "from", itemRequestListTO.getFrom(),
                "size", itemRequestListTO.getSize()
        );
        return get("/search?text={text}&from={from}&size={size}", itemRequestListTO.getIdUser(), parameters);
    }

    public ResponseEntity<Object> deleteItemById(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentCreateTO commentCreateTO) {
        return post("/" + itemId + "/comment", userId, commentCreateTO);
    }

}
