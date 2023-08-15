package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    User user;
    User user2;
    UserDto userDto;
    UserDto userDto2;
    List<UserDto> listDto;
    List<User> list;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "name", "user@user.ru");
        user2 = new User(2L, "name2", "user2@user.ru");
        userDto = new UserDto(1L, "name", "user@user.ru");
        userDto2 = new UserDto(2L, "name2", "user2@user.ru");
        listDto = new ArrayList<>(asList(userDto));
        list = new ArrayList<>(asList(user));
    }

    @Test
    void getUsers() throws Exception {
        when(userService.getUsers()).thenReturn(list);
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto);
        mvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class));
    }

    @Test
    void updateUserByIdPatch() throws Exception {
        when(userService.updateUserById(anyLong(), any()))
                .thenReturn(user2);
        mvc.perform(patch("/users/{userId}", user2.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Long.class));
    }

    @Test
    void deleteUserById() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());
    }
}