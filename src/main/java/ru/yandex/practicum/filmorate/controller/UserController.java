package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) {
        idValidityCheck(id);
        return userStorage.findUser(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        idValidityCheck(id);
        idValidityCheck(friendId);
        idInequalityCheck(id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        idValidityCheck(id);
        idValidityCheck(friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        idValidityCheck(id);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        idValidityCheck(id);
        idInequalityCheck(id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    private void idValidityCheck(Long id) {
        if (id == null || id < 1) {
            String message = "Некорректное значение параметра id" +
                    "пользователя, должно быть больше 0";
            String value = String.valueOf(id);
            log.error(value, message);
            throw new ParameterNotValidException(value, message);
        }
    }

    private void idInequalityCheck(long id, long friendId) {
        if (id == friendId) {
            String message = "Переданы одинаковые параметры id " +
                    "пользователей";
            String value = String.valueOf(id);
            log.error(value, message);
            throw new ParameterNotValidException(value, message);
        }
    }

}
