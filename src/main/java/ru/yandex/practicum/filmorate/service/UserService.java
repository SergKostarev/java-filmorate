package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(long id, long friendId) {
        User user = userStorage.findUser(id);
        User friend = userStorage.findUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.findUser(id);
        User friend = userStorage.findUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(long id) {
        User user = userStorage.findUser(id);
        return userStorage
                .findAll()
                .stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .toList();
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.findUser(id);
        User other = userStorage.findUser(otherId);
        return userStorage
                .findAll()
                .stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .filter(u -> other.getFriends().contains(u.getId()))
                .toList();
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User findUser(Long id) {
        return userStorage.findUser(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }
}
