package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Service.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    ItemDto item;
    CommentDto commentDto;
    List<ItemDto> listDto;

    @BeforeEach
    public void setUp() {
        item = new ItemDto(1L, "name", "Description", true, null, null,
                null, 1L);
        commentDto = new CommentDto(1L, "text", "author", LocalDateTime.now());
        listDto = new ArrayList<>(asList(item));
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(anyLong(), anyInt(), anyInt())).thenReturn(listDto);
        mvc.perform(get("/items").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsText() throws Exception {
        when(itemService.getItemsText(anyString(), anyInt(), anyInt())).thenReturn(listDto);
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);
        mvc.perform(get("/items/{id}", 1L).header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(item);
        mvc.perform(post("/items").header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(any(), anyLong(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1L).header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserByIdPatch() throws Exception {
        when(itemService.updateItemById(anyLong(), anyLong(), any()))
                .thenReturn(item);
        mvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class));
    }

    @Test
    void deleteUserById() throws Exception {
        mvc.perform(delete("/items/{id}", 1L))
                .andExpect(status().isOk());
    }
}