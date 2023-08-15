package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    ItemRequestDto itemRequestDto;
    ItemRequestDto itemRequestDto2;
    List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        User user = new User(1L, "name", "user@user.ru");
        itemRequestDto = new ItemRequestDto(1L, "descriptionDto", user, LocalDateTime.now(), null);
        itemRequestDto2 = new ItemRequestDto(2L, "descriptionDto2", user, LocalDateTime.now(), null);
        itemRequestDtoList = new ArrayList<>(asList(itemRequestDto, itemRequestDto2));
    }

    @Test
    void getRequests() throws Exception {
        when(itemRequestService.getRequests(anyLong())).thenReturn(itemRequestDtoList);
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsFrom() throws Exception {
        when(itemRequestService.getRequestsFrom(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestDtoList);
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsById() throws Exception {
        when(itemRequestService.getRequestsById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1L).header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createRequests() throws Exception {
        when(itemRequestService.createRequests(anyLong(), any())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests").header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}