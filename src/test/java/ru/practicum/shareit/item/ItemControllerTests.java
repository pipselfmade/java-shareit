package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void getItems() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        List<ItemDto> expectedResult = new ArrayList<>();

        when(itemService.getItems(userId))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResult.size())));
    }

    @Test
    public void searchItems() throws Exception {
        String text = "search";
        int from = 0;
        int size = 10;
        List<ItemDto> expectedResult = new ArrayList<>();

        when(itemService.searchItems(text, from, size))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResult.size())));
    }

    @Test
    public void searchItems_InvalidPageFrom() throws Exception {
        String text = "search";
        int from = -1;
        int size = 10;

        when(itemService.searchItems(text, from, size))
                .thenThrow(new ValidationException(""));

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void searchItems_InvalidPageSize() throws Exception {
        String text = "search";
        int from = 0;
        int size = -1;
        when(itemService.searchItems(text, from, size))
                .thenThrow(new ValidationException(""));

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();
        ItemDto expectedResult = ItemDto.builder()
                .id(1L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();

        when(itemService.updateItem(userId, itemDto, itemId))
                .thenReturn(expectedResult);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(expectedResult.getId().intValue())))
                .andExpect(jsonPath("$.description", Matchers.is(expectedResult.getDescription())))
                .andExpect(jsonPath("$.name", Matchers.is(expectedResult.getName())))
                .andExpect(jsonPath("$.requestId", Matchers.is(expectedResult.getRequestId().intValue())))
                .andExpect(jsonPath("$.available", Matchers.is(expectedResult.getAvailable())));
    }

    @Test
    public void updateItem_InvalidUser() throws Exception {
        Long userId = 99L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();

        when(itemService.updateItem(userId, itemDto, itemId)).thenThrow(new NotFoundException("User not found!"));
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateItem_InvalidItem() throws Exception {
        Long userId = 1L;
        Long itemId = 99L;
        ItemDto itemDto = ItemDto.builder()
                .id(99L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();

        when(itemService.updateItem(userId, itemDto, itemId))
                .thenThrow(new NotFoundException("Item not found!"));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addItem() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();
        ItemDto expectedResult = ItemDto.builder()
                .id(1L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();

        when(itemService.addItem(userId, itemDto))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(expectedResult.getId().intValue())))
                .andExpect(jsonPath("$.description", Matchers.is(expectedResult.getDescription())))
                .andExpect(jsonPath("$.name", Matchers.is(expectedResult.getName())))
                .andExpect(jsonPath("$.requestId", Matchers.is(expectedResult.getRequestId().intValue())))
                .andExpect(jsonPath("$.available", Matchers.is(expectedResult.getAvailable())));
    }

    @Test
    public void addItem_InvalidUser() throws Exception {
        Long userId = 99L;
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("Test")
                .name("Test")
                .requestId(1L)
                .available(true).build();

        when(itemService.addItem(userId, itemDto))
                .thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addItem_InvalidItem() throws Exception {
        Long userId = 99L;
        ItemDto itemDto = ItemDto.builder().id(1L).description("").name("").build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(1L)
                .authorName("Test")
                .text("test").build();
        CommentDto expectedResult = CommentDto.builder()
                .id(1L)
                .itemId(1L)
                .authorName("Test")
                .text("test").build();

        when(itemService.addComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentDto.class)))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(expectedResult.getId().intValue())))
                .andExpect(jsonPath("$.itemId", Matchers.is(expectedResult.getItemId().intValue())))
                .andExpect(jsonPath("$.authorName", Matchers.is(expectedResult.getAuthorName())))
                .andExpect(jsonPath("$.text", Matchers.is(expectedResult.getText())));
    }

    @Test
    public void addComment_InvalidUser() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(1L)
                .authorName("Test")
                .text("test").build();
        when(itemService.addComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentDto.class)))
                .thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addComment_InvalidComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(1L)
                .authorName("Test")
                .text("").build();

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
