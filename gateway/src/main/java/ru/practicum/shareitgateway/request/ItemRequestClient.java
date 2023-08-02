package ru.practicum.shareitgateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String itemRequestUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(itemRequestUrl + "/requests"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getRequestsFrom(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestsById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> createRequests(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }
}
