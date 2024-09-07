package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public void like(long id, long userId) {
        Film film = filmStorage.findFilm(id);
        User user = userStorage.findUser(userId);
        film.getLikes().add(user.getId());
    }

    public void dislike(long id, long userId) {
        Film film = filmStorage.findFilm(id);
        User user = userStorage.findUser(userId);
        film.getLikes().remove(user.getId());
    }

    public List<Film> getMostPopular(int count) {
        Comparator<Film> comparator = Comparator.comparing(f -> f.getLikes().size());
        return filmStorage.findAll()
                .stream()
                .sorted(comparator.reversed())
                .limit(count)
                .toList();

    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilm(Long id) {
        return filmStorage.findFilm(id);
    }
}
