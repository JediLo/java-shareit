package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public User findUserByID(Long userId) {
        return users.get(userId);
    }

    @Override
    public User save(User user) {
        user.setId(nextId);
        users.put(nextId, user);
        nextId++;
        return user;
    }


    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsEmailWithoutUserEmail(String email, Long userId) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email) && !user.getId().equals(userId));
    }

}