package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public void like(long filmId, long userId) {
        filmStorage.findFilm(filmId);
        userStorage.findUser(userId);
        filmStorage.like(filmId, userId);
    }

    public void dislike(long filmId, long userId) {
        filmStorage.findFilm(filmId);
        userStorage.findUser(userId);
        filmStorage.dislike(filmId, userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getMostPopular(count);
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            processError("Id фильма должен быть указан", newFilm);
        }
        isValid(newFilm);
        processGenresAndRating(newFilm);
        return filmStorage.update(newFilm);
    }

    public Film create(Film film) {
        isValid(film);
        processGenresAndRating(film);
        return filmStorage.create(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilm(Long id) {
        return filmStorage.findFilm(id);
    }

    private void isValid(Film film) {
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            processError("Дата выхода фильма должна быть позже 28 декабря 1895", film);
        }
    }

    private void processError(String message, Film film) {
        log.error(message, film);
        throw new ValidationException(message);
    }

    private void processGenresAndRating(Film film) {
        film.setGenres(new ArrayList<>(new LinkedHashSet<>(film.getGenres())));
        for (Genre genre : film.getGenres()) {
            try {
                genre.setName(filmStorage.getGenre(genre.getId()).getName());
            } catch (NotFoundException e) {
                throw new ValidationException("Жанр не найден");
            }
        }
        if (film.getMpa() != null) {
            try {
                film.getMpa().setName(filmStorage.getRating(film.getMpa().getId()).getName());
            } catch (NotFoundException e) {
                throw new ValidationException("Рейтинг не найден");
            }
        }
    }
}
