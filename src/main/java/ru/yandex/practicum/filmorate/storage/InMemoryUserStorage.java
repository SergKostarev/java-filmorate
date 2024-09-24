package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Set<Long> getFriends(long id) {
        return friends.get(id);
    }

    @Override
    public void addFriend(long id, long friendId) {
        getFriends(id).add(friendId);
    }

    /*
    @Override
    public void confirmFriend(long id, long friendId) {
        if (!getFriends(friendId).contains(id)) {
            log.error(String.valueOf(id), "Пользователь не найден в списке друзей пользователя," +
                    "невозможно подтвердить дружбу");
            throw new NotFoundException(String.valueOf(friendId), "Пользователь не найден в списке друзей пользователя," +
                    "невозможно подтвердить дружбу");
        }
        getFriends(id).add(friendId);
    }
    */

    @Override
    public void deleteFriend(long id, long friendId) {
        getFriends(id).remove(friendId);
    }

    @Override
    public User findUser(long id) {
        if (users.get(id) == null) {
            log.error(String.valueOf(id), "Пользователь не найден");
            throw new NotFoundException(String.valueOf(id), "Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        log.info("Пользователь успешно добавлен", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = findUser(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Пользователь успешно обновлен", oldUser);
        return oldUser;
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
