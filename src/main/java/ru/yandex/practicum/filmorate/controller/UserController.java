package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        isValid(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            String message = "Id пользователя должен быть указан";
            log.error(message, newUser);
            throw new ValidationException(message);
        }
        isValid(newUser);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь успешно обновлен", oldUser);
            return oldUser;
        }
        String message = "Пользователь с id = " + newUser.getId() + " не найден";
        log.error(message, newUser);
        throw new ValidationException(message);
    }

    private void isValid(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            String message = "Некорректный email";
            log.error(message, user);
            throw new ValidationException(message);
        }
        if (user.getLogin() == null || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            String message = "Некорректный логин";
            log.error(message, user);
            throw new ValidationException(message);
        }
        if (user.getBirthday() != null
                && user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть позже "
                    + LocalDate.now();
            log.error(message, user);
            throw new ValidationException(message);
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
