package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmControllerTest {
    FilmController filmController;
    Validator validator;

    @BeforeEach
    public void getController() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        filmController = new FilmController(new FilmService(filmStorage, userStorage));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void givenCorrectFilm_shouldCreateFilm() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1L, null));
        Film film = new Film(null, "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60, genres, new Rating(1L, null));
        Film filmReturned = filmController.create(film);
        Assertions.assertNotNull(filmReturned);
        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void givenIncorrectFilm_shouldNotCreateFilm() {
        Film film = new Film(null, "Test name", "Test description",
                LocalDate.of(2010, 1, 1), -60, null, null);
        assertThat(validator.validate(film).size()).isEqualTo(1);
    }

    @Test
    public void givenCorrectFilm_shouldUpdateFilm() {
        Film film = new Film(null, "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60, null, null);
        Film filmReturned = filmController.create(film);
        Assertions.assertNotNull(filmReturned);
        Film filmUpdated = filmController.update(new Film(1L,
                "Test name 2", "Test description 2",
                LocalDate.of(2010, 1, 1), 60, null, null));
        Assertions.assertNotNull(filmUpdated);
        Assertions.assertEquals(film.getName(), "Test name 2");
        Assertions.assertEquals(film.getDescription(), "Test description 2");
    }

    @Test
    public void givenIncorrectFilm_shouldNotUpdateFilm() {
        Film filmReturned = filmController.create(new Film(null,
                "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60, null, null));
        Assertions.assertNotNull(filmReturned);
        assertThat(validator.validate(new Film(1L,
                "Test name 2", "Test description 2",
                LocalDate.of(2010, 1, 1), -60, null, null)).size()).isEqualTo(1);
    }

    @Test
    public void givenCorrectFilms_shouldReturnFilmCollection() {
        Film filmReturned = filmController.create(new Film(null,
                "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60, null, null));
        Film filmReturned2 = filmController.create(new Film(null,
                "Test name 2", "Test description 2",
                LocalDate.of(2010, 1, 1), 60, null, null));
        Assertions.assertNotNull(filmReturned);
        Assertions.assertNotNull(filmReturned2);
        Assertions.assertEquals(2, filmController.findAll().size());
    }

}
