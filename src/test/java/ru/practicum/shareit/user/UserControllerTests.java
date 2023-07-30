package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.request.ItemRequestControllerTests.asJsonString;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getUsers() throws Exception {
        Mockito.when(userService.getUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void getUserById_invalidId() throws Exception {
        Long invalidId = 100L;
        Mockito.when(userService.getUserById(invalidId))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUser() throws Exception {
        Long id = 1L;
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        Mockito.when(userService.addUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUser() throws Exception {
        Long id = 1L;
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        Mockito.when(userService.updateUser(Mockito.eq(id), Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + id)
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUser_invalidId() throws Exception {
        Long invalidId = 100L;
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@test.com")
                .build();

        Mockito.when(userService.updateUser(Mockito.eq(invalidId), Mockito.any(UserDto.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + invalidId)
                        .content(asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
