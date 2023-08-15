package ru.practicum.shareitgateway.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    public ItemClient(@Value("${shareit-server.url}") String itemUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(itemUrl + "/items"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getItems(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemsText(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Long userId, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateItemById(Long userId, Long itemId, ItemDto item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> deleteItemById(Long id) {
        return delete("/" + id);
    }
}
