package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    User findUser(long id);

    User create(User user);

    User update(User newUser);

    Collection<User> findAll();

    Set<Long> getFriends(long id);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);
}
