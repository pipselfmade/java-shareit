package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@ActiveProfiles("test")
public class ItemDtoJsonTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serializeToJson() throws JsonProcessingException {
        User owner = User.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("TestItem")
                .description("TestItem")
                .available(true)
                .owner(owner)
                .requestId(2L)
                .build();
        String expectedJson = "{\"id\":1,\"name\":\"TestItem\",\"description\":\"TestItem\"," +
                "\"available\":true,\"owner\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"},\"requestId\":2,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null}";
        String actualJson = objectMapper.writeValueAsString(itemDto);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void deserializeFromJson() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"Item\",\"description\":\"This is an item\",\"available\":true,\"owner\"" +
                ":{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"},\"requestId\":2}";
        User owner = User.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("This is an item")
                .available(true)
                .owner(owner)
                .requestId(2L)
                .build();
        ItemDto actualItemDto = objectMapper.readValue(json, ItemDto.class);

        assertEquals(expectedItemDto, actualItemDto);
    }
}
