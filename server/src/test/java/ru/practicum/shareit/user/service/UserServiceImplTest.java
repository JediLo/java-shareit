package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.createUserById;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    //getUserById()
    @Test
    void shouldReturnUserDtoWhenUserExists() {

        Long userId = 1L;
        User user = createUserById(userId);
        user.setEmail("email@email");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository).findById(userId);
    }

    //saveUser()
    @Test
    void shouldSaveUserWhenEmailIsUnique() {
        Long userId = 1L;
        User user = createUserById(userId);
        user.setEmail("email@email");

        UserDto requestDto = UserMapperDto.toUserDto(user);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArgument(0));

        UserDto result = userService.saveUser(requestDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(any(User.class));

    }

    @Test
    void shouldThrowDuplicatedDataExceptionWhenEmailAlreadyExists() {
        String email = "email@email";
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);
        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.saveUser(userDto));

        assertEquals("Такая почта уже зарегистрирована", exception.getMessage());

        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    //updateUser()
    @Test
    void shouldUpdateNameAndEmailWhenBothProvided() {
        Long userId = 1L;

        User fromDb = createUserById(userId);
        fromDb.setEmail("oldEmail@oldEmail");
        User user = createUserById(userId);
        user.setName("newName");
        user.setEmail("newEmail@email");

        UserDto requestDto = UserMapperDto.toUserDto(user);

        when(userRepository.existsByEmailAndIdNot(user.getEmail(), userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fromDb));
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser(userId, requestDto);
        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).existsByEmailAndIdNot(user.getEmail(), userId);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateOnlyEmailWhenNameIsNull() {
        Long userId = 1L;

        User fromDb = createUserById(userId);
        fromDb.setEmail("oldEmail@oldEmail");
        User user = createUserById(userId);
        user.setName(null);
        user.setEmail("newEmail@email");

        UserDto requestDto = UserMapperDto.toUserDto(user);

        when(userRepository.existsByEmailAndIdNot(user.getEmail(), userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fromDb));
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser(userId, requestDto);
        assertNotNull(result);
        assertEquals(fromDb.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).existsByEmailAndIdNot(user.getEmail(), userId);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateOnlyNameWhenEmailIsNull() {
        Long userId = 1L;

        User fromDb = createUserById(userId);
        fromDb.setEmail("oldEmail@oldEmail");
        User user = createUserById(userId);

        UserDto requestDto = UserMapperDto.toUserDto(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(fromDb));
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser(userId, requestDto);
        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(fromDb.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowDuplicatedDataExceptionWhenUpdatingWithExistingEmail() {
        Long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setEmail("email@email");

        when(userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)).thenReturn(true);
        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.updateUser(userId, userDto));

        assertEquals("Такая почта уже зарегистрирована", exception.getMessage());

        verify(userRepository).existsByEmailAndIdNot(userDto.getEmail(), userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistingUser() {
        Long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setEmail("email@email");

        when(userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, userDto));

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository).existsByEmailAndIdNot(userDto.getEmail(), userId);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    // deleteUser()
    @Test
    void shouldDeleteUserWhenUserExists() {

        Long userId = 1L;

        User user = createUserById(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);

    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistingUser() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(userId));

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}