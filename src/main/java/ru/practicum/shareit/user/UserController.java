package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") @NotNull Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody @NotNull UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable("userId") @NotNull Long userId,
                             @RequestBody @NotNull UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") @NotNull Long userId) {
        userService.deleteUser(userId);
    }
}