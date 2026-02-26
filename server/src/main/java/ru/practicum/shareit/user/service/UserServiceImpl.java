package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        return UserMapperDto.toUserDto(repository.findById(userId).orElseThrow(() -> userNotFound(userId)));
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new DuplicatedDataException("Такая почта уже зарегистрирована");
        }
        User user = UserMapperDto.toUser(userDto);
        User savedUser = repository.save(user);
        return UserMapperDto.toUserDto(savedUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userDto.getEmail() != null && repository.existsByEmailAndIdNot(userDto.getEmail(), userId)) {
            throw new DuplicatedDataException("Такая почта уже зарегистрирована");
        }
        User user = repository.findById(userId).orElseThrow(() -> userNotFound(userId));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User saved = repository.save(user);
        return UserMapperDto.toUserDto(saved);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        repository.findById(userId).orElseThrow(() -> userNotFound(userId));
        repository.deleteById(userId);
    }

    private NotFoundException userNotFound(Long userId) {
        return new NotFoundException("Пользователь c ID " + userId + " не найден");
    }
}