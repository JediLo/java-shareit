package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    User findUserByID(Long userId);

    User save(User user);

    void deleteUser(Long userId);

    boolean existsEmail(String email);

    boolean existsEmailWithoutUserEmail(String email, Long userId);
}