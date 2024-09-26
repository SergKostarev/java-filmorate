package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Set<Long> getLikes(long id);

    Collection<Film> findAll();

    Film findFilm(long id);

    Film create(Film film);

    Film update(Film newFilm);

    void dislike(long filmId, long userId);

    void like(long filmId, long userId);

    List<Film> getMostPopular(int count);

    Collection<Rating> getAllRatings();

    Rating getRating(long id);

    Collection<Genre> getAllGenres();

    Genre getGenre(long id);
}
