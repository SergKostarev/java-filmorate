package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(long id, long friendId) {
        userStorage.findUser(id);
        userStorage.findUser(friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        userStorage.findUser(id);
        userStorage.findUser(friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(long id) {
        userStorage.findUser(id);
        Set<Long> friends = userStorage.getFriends(id);
        return userStorage
                .findAll()
                .stream()
                .filter(u -> friends.contains(u.getId()))
                .toList();
    }

    public List<User> getCommonFriends(long id, long otherId) {
        Set<Long> friends = userStorage.getFriends(id);
        Set<Long> otherFriends = userStorage.getFriends(otherId);
        friends.retainAll(otherFriends);
        return userStorage
                .findAll()
                .stream()
                .filter(u -> friends.contains(u.getId()))
                .toList();
    }

    public User update(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.update(newUser);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User findUser(long id) {
        return userStorage.findUser(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }
}
