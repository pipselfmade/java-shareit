package ru.practicum.shareit.user.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void makeUser() throws BadRequestException {
        User user = easyRandom.nextObject(User.class);
        UserDto userDto = UserMapper.makeUserDto(user);
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void makeUserDto() throws BadRequestException {
        UserDto userDto = easyRandom.nextObject(UserDto.class);
        User user = UserMapper.makeUser(userDto);
        assertEquals(user.getName(), userDto.getName());
    }
}