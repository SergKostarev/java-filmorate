package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void getController() {
        filmController = new FilmController();
    }

    @Test
    public void givenCorrectFilm_shouldCreateFilm() {
        Film film = new Film(null, "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60);
        Film filmReturned = filmController.create(film);
        Assertions.assertNotNull(filmReturned);
        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void givenIncorrectFilm_shouldNotCreateFilm() {
        Film film = new Film(null, "Test name", "Test description",
                LocalDate.of(2010, 1, 1), -60);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void givenCorrectFilm_shouldUpdateFilm() {
        Film film = new Film(null, "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60);
        Film filmReturned = filmController.create(film);
        Assertions.assertNotNull(filmReturned);
        Film filmUpdated = filmController.update(new Film(1L,
                "Test name 2", "Test description 2",
                LocalDate.of(2010, 1, 1), 60));
        Assertions.assertNotNull(filmUpdated);
        Assertions.assertEquals(film.getName(), "Test name 2");
        Assertions.assertEquals(film.getDescription(), "Test description 2");
    }

    @Test
    public void givenIncorrectFilm_shouldNotUpdateFilm() {
        Film filmReturned = filmController.create(new Film(null,
                "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60));
        Assertions.assertNotNull(filmReturned);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(new Film(1L,
                "Test name 2", "Test description 2",
                LocalDate.of(2010, 1, 1), -60)));
    }

    @Test
    public void givenCorrectFilms_shouldReturnFilmCollection() {
        Film filmReturned = filmController.create(new Film(null,
                "Test name", "Test description",
                LocalDate.of(2010, 1, 1), 60));
        Film filmReturned2 = filmController.create(new Film(null,
                "Test name 2", "Test description 2",
                LocalDate.of(2010, 1, 1), 60));
        Assertions.assertNotNull(filmReturned);
        Assertions.assertNotNull(filmReturned2);
        Assertions.assertEquals(2, filmController.findAll().size());
    }

}
