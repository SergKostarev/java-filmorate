package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idInequalityCheck;
import static ru.yandex.practicum.filmorate.validation.ValidationUtils.idValidityCheck;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService
                .findAll()
                .stream()
                //.map(UserMapper::mapToUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id пользователя, должно быть больше 0");
        return userService.findUser(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        idValidityCheck(id, "Некорректное значение параметра id пользователя, должно быть больше 0");
        idValidityCheck(friendId, "Некорректное значение параметра id пользователя, должно быть больше 0");
        idInequalityCheck(id, friendId, "Переданы одинаковые параметры id пользователей");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        idValidityCheck(id, "Некорректное значение параметра id пользователя, должно быть больше 0");
        idValidityCheck(friendId, "Некорректное значение параметра id пользователя, должно быть больше 0");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        idValidityCheck(id, "Некорректное значение параметра id пользователя, должно быть больше 0");
        return userService
                .getFriends(id)
                .stream()
                .toList();
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        idValidityCheck(id, "Некорректное значение параметра id пользователя, должно быть больше 0");
        idInequalityCheck(id, otherId, "Переданы одинаковые параметры id пользователей");
        return userService
                .getCommonFriends(id, otherId)
                .stream()
                .toList();
    }

}
