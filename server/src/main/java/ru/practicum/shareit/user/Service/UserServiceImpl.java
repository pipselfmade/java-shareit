package ru.practicum.shareit.user.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Long id = 0L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<User>(userRepository.findAll());
    }

    @Transactional
    @Override
    public UserDto getUserById(Long idUser) {
        if (idUser > returnId()) {
            throw new NotFoundException("Заданный Id отсутствует (User)");
        }

        User user = userRepository.getById(idUser);
        UserDto userDto = UserMapper.makeUserDto(user);
        userDto.setId(user.getId());
        return userDto;
    }

    @Transactional
    @Override
    public User createUser(UserDto user) throws CloneNotSupportedException {
        User newUser = UserMapper.makeUser(user);

        if (newUser.getEmail() == (null)) {
            throw new BadRequestException("Поле email не заполнено (User)");
        }
        if (!newUser.getEmail().contains("@")) {
            throw new BadRequestException("Неправильный email(User)");
        }

        newUser.setId(makeId());
        List<User> userList = userRepository.findAll();

        for (User us : userList) {
            User userM = us;
            if (userM.getEmail().equals(newUser.getEmail()))
                throw new CloneNotSupportedException("Такой email уже существует(User)");
        }

        userRepository.save(newUser);
        return newUser;
    }

    @Transactional
    @Override
    public User updateUserById(Long id, UserDto userDto) throws CloneNotSupportedException {
        User adUser = UserMapper.makeUser(userDto);
        adUser.setId(id);

        if (!(userDto.getEmail() == null)) {
            adUser.setEmail(userDto.getEmail());
        } else {
            adUser.setEmail(userRepository.getById(id).getEmail());
        }

        List<User> userList = userRepository.findAll();

        for (User us : userList) {
            User userM = us;
            if (adUser.getId().compareTo(userM.getId()) != 0) {
                if (userM.getEmail().equals(adUser.getEmail()))
                    throw new CloneNotSupportedException("Данный email уже зарегистрирован");
            }
        }

        if (!(adUser.getName() == null)) {
            adUser.setName(userDto.getName());
        } else {
            adUser.setName(userRepository.getById(id).getName());
        }

        userRepository.save(adUser);
        return adUser;
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Long returnId() {
        return id;
    }

    public void setId(Long ids) {
        id = ids;
    }

    private Long makeId() {
        id += 1;
        return id;
    }
}
