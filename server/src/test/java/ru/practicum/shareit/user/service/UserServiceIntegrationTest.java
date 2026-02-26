package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserToDatabase() {

        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("Email@email");

        UserDto responseDto = userService.saveUser(userDto);
        assertNotNull(responseDto.getId());
        assertEquals(userDto.getName(), responseDto.getName());
        assertEquals(userDto.getEmail(), responseDto.getEmail());

        User user = userRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void shouldUpdateUserInDatabase() {

        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        UserDto userDto = new UserDto();
        userDto.setName("NewName");
        userDto.setEmail("New@email");
        UserDto responseDto = userService.updateUser(userSaved.getId(), userDto);
        assertEquals(userSaved.getId(), responseDto.getId());
        assertEquals(userDto.getName(), responseDto.getName());
        assertEquals(userDto.getEmail(), responseDto.getEmail());

        User userFromDb = userRepository.findById(userSaved.getId()).orElseThrow();
        assertEquals(userDto.getName(), userFromDb.getName());
        assertEquals(userDto.getEmail(), userFromDb.getEmail());
    }

    @Test
    void shouldDeleteUserFromDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        userService.deleteUser(userSaved.getId());
        Optional<User> userFromDb = userRepository.findById(userSaved.getId());
        assertTrue(userFromDb.isEmpty());
    }

    @Test
    void shouldFindUserByIdFromDatabase() {
        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User userSaved = userRepository.save(user);

        UserDto userFromDb = userService.getUserById(userSaved.getId());

        assertEquals(userSaved.getId(), userFromDb.getId());
        assertEquals(userSaved.getName(), userFromDb.getName());
        assertEquals(userSaved.getEmail(), userFromDb.getEmail());
    }

}