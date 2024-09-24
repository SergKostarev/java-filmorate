package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("inMemoryFilmStorage")
//@Primary
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private final List<Rating> ratings = List.of(
            new Rating (1L, "G"),
            new Rating (2L, "PG"),
            new Rating (3L, "PG-13"),
            new Rating (4L, "R"),
            new Rating (5L, "NC-17")
    );

    private final List<Genre> genres = List.of(
            new Genre (1L, "Комедия"),
            new Genre (2L, "Драма"),
            new Genre (3L, "Мультфильм"),
            new Genre (4L, "Триллер"),
            new Genre (5L, "Документальный"),
            new Genre (6L, "Боевик")
    );

    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Set<Long> getLikes(long id) {
        if (likes.get(id) == null) {
            throw new NotFoundException("Фильм не найден", String.valueOf(id));
        }
        return likes.get(id);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findFilm(long id) {
        if (films.get(id) == null) {
            log.error(String.valueOf(id), "Фильм не найден");
            throw new NotFoundException(String.valueOf(id), "Фильм не найден");
        }
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        log.info("Фильм успешно добавлен", film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film oldFilm = findFilm(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setGenres(newFilm.getGenres());
        oldFilm.setMpa(newFilm.getMpa());
        log.info("Фильм успешно обновлен", oldFilm);
        return oldFilm;
    }

    @Override
    public void dislike(long filmId, long userId) {
        likes.get(filmId).remove(userId);
    }

    @Override
    public void like(long filmId, long userId) {
        likes.get(filmId).add(userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        Comparator<Film> comparator = Comparator.comparing(f -> getLikes(f.getId()).size());
        return findAll()
                .stream()
                .sorted(comparator.reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Collection<Rating> getAllRatings() {
        return ratings;
    }

    @Override
    public Rating getRating(long id) {
        Optional<Rating> rating = ratings
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
        if (rating.isEmpty()) {
            throw new NotFoundException(String.valueOf(id), "Рейтинг не найден");
        }
        return rating.get();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genres;
    }

    @Override
    public Genre getGenre(long id) {
        Optional<Genre> genre = genres
                .stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();
        if (genre.isEmpty()) {
            throw new NotFoundException(String.valueOf(id), "Жанр не найден");
        }
        return genre.get();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void processError(String message, Film film) {
        log.error(message, film);
        throw new ValidationException(message);
    }

}
