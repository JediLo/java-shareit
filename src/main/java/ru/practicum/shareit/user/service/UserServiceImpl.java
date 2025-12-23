package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;


    @Override
    public UserDto getUserById(Long userId) {
        validationUserId(userId);
        User user = repository.findUserByID(userId);
        validationUser(user);
        return UserMapperDto.toUserDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        validationUserDto(userDto);
        if (repository.existsEmail(userDto.getEmail())) {
            throw new DuplicatedDataException("Такая почта уже зарегистрирована");
        }
        User user = UserMapperDto.toUser(userDto);
        User savedUser = repository.save(user);
        return UserMapperDto.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        validationUserId(userId);
        validationUserDto(userDto);
        if (userDto.getEmail() != null && repository.existsEmailWithoutUserEmail(userDto.getEmail(), userId)) {
            throw new DuplicatedDataException("Такая почта уже зарегистрирована");
        }
        User user = repository.findUserByID(userId);
        validationUser(user);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapperDto.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        validationUserId(userId);
        validationUser(repository.findUserByID(userId));
        repository.deleteUser(userId);
    }

    private void validationUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    private void validationUserDto(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("Передан пустой пользователь");
        }
    }

    private void validationUser(User user) {
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}