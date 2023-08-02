package ru.practicum.shareitgateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;

@Service
public class UserClient extends BaseClient {
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String userUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(userUrl + "/users"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> createUser(UserDto user) {
        return post("", user);
    }

    public ResponseEntity<Object> updateUserById(Long userId, UserDto user) {
        return patch("/" + userId, user);
    }

    public ResponseEntity<Object> deleteUserById(Long id) {
        return delete("/" + id);
    }
}
