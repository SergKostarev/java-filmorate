package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru/yandex/practicum/filmorate/storage")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "classpath:/initialize-data.sql")
class FilmoRateApplicationTests {

	@Autowired
	private final FilmStorage filmStorage;

	@Autowired
	private final UserStorage userStorage;

	@Test
	@Order(1)
	public void givenCorrectFilms_shouldCreateFilms() {
		List<Genre> genres = new ArrayList<>();
		genres.add(new Genre(1L, null));
		Film film = new Film(null, "Test name 3", "Test description 3",
				LocalDate.of(2010, 1, 1), 60, genres, new Rating(1L, null));
		assertEquals(3L, filmStorage.create(film).getId().longValue());
		List<Genre> genres2 = new ArrayList<>();
		genres2.add(new Genre(2L, null));
		Film film2 = new Film(null, "Test name 4", "Test description 4",
				LocalDate.of(2020, 1, 1), 100, genres2, new Rating(2L, null));
		assertEquals(4L, filmStorage.create(film2).getId().longValue());
		assertEquals(4, filmStorage.findAll().size());
	}

	@Test
	@Order(2)
	public void givenIncorrectFilm_shouldNotCreateFilm() {
		List<Genre> genres = new ArrayList<>();
		genres.add(new Genre(1L, null));
		Film film = new Film(null, "Test name 3", "Test description 3",
				LocalDate.of(2010, 1, 1), -60, genres, new Rating(1L, null));
		assertThrows(DataIntegrityViolationException.class, () -> filmStorage.create(film));
	}

	@Test
	@Order(3)
	public void givenCorrectFilm_shouldUpdateFilm() {
		List<Genre> genres2 = new ArrayList<>();
		genres2.add(new Genre(1L, null));
		Film film2 = new Film(8L, "Test name 2", "Test description 2",
				LocalDate.of(2020, 1, 1), 100, genres2, new Rating(2L, null));
		assertEquals("Test name 2", filmStorage.update(film2).getName());
	}

	@Test
	@Order(4)
	public void shouldFindTwoFilms() {
		assertEquals(2, filmStorage.findAll().size());
	}

	@Test
	@Order(5)
	public void shouldFindFilm() {
		assertEquals(12, filmStorage.findFilm(12).getId());
	}

	@Test
	@Order(6)
	public void shouldLike() {
		filmStorage.like(14, 11);
		Set<Long> likes = filmStorage.getLikes(14);
		assertEquals(1, likes.size());
		assertTrue(likes.contains(11L));
	}

	@Test
	@Order(7)
	public void shouldDislike() {
		filmStorage.like(16, 13);
		filmStorage.dislike(16, 13);
		Set<Long> likes = filmStorage.getLikes(16);
		assertTrue(likes.isEmpty());
	}

	@Test
	@Order(8)
	public void shouldGetMostPopular() {
		filmStorage.like(18, 15);
		filmStorage.like(19, 15);
		filmStorage.like(18, 16);
		List<Film> likes = filmStorage.getMostPopular(2);
		assertEquals(2, likes.size());
		assertEquals(18, likes.get(0).getId());
		assertEquals(19, likes.get(1).getId());
	}

	@Test
	@Order(9)
	public void givenCorrectRatingId_shouldGetRating() {
		assertEquals(1, filmStorage.getRating(1).getId());
		assertEquals("G", filmStorage.getRating(1).getName());
	}

	@Test
	@Order(10)
	public void givenIncorrectRatingId_shouldNotGetRating() {
		assertThrows(NotFoundException.class, () -> filmStorage.getRating(10));
	}

	@Test
	@Order(11)
	public void givenCorrectGenreId_shouldGetGenre() {
		assertEquals(1, filmStorage.getGenre(1).getId());
		assertEquals("Комедия", filmStorage.getGenre(1).getName());
	}

	@Test
	@Order(12)
	public void givenIncorrectGenreId_shouldNotGetGenre() {
		assertThrows(NotFoundException.class, () -> filmStorage.getGenre(10));
	}

	@Test
	@Order(13)
	public void shouldGiveAllGenres() {
		assertEquals(6, filmStorage.getAllGenres().size());
	}

	@Test
	@Order(14)
	public void shouldGiveAllRatings() {
		assertEquals(5, filmStorage.getAllRatings().size());
	}



	@Test
	@Order(15)
	public void givenCorrectUsers_shouldCreateUsers() {
		User user = new User(null, "example@example.com", "Test_login",
				"Test name", LocalDate.of(2000, 1, 1));
		User user2 = new User(null, "example2@example.com", "Test_login2",
				"Test name2", LocalDate.of(1990, 1, 1));
		assertEquals(31L, userStorage.create(user).getId().longValue());
		assertEquals(32L, userStorage.create(user2).getId().longValue());
		assertEquals(4, userStorage.findAll().size());
	}

	@Test
	@Order(16)
	public void givenIncorrectUser_shouldNotCreateUser() {
		User user = new User(null, "", "Test_login",
				"Test name", LocalDate.of(2000, 1, 1));
		assertThrows(DataIntegrityViolationException.class, () -> userStorage.create(user));
	}

	@Test
	@Order(17)
	public void givenCorrectUser_shouldUpdateUser() {
		User user = new User(36L, "example2@example.com", "Test_login3",
				"Test name3", LocalDate.of(1990, 1, 1));
		assertEquals("Test name3", userStorage.update(user).getName());
	}

	@Test
	@Order(18)
	public void shouldFindTwoUsers() {
		assertEquals(2, userStorage.findAll().size());
	}

	@Test
	@Order(19)
	public void shouldFindUser() {
		assertEquals(40, userStorage.findUser(40).getId());
	}

	@Test
	@Order(20)
	public void shouldAddFriend() {
		userStorage.addFriend(42, 43);
		Set<Long> friends = userStorage.getFriends(42);
		assertEquals(1, friends.size());
		assertTrue(friends.contains(43L));
	}

	@Test
	@Order(21)
	public void shouldDeleteFriend() {
		userStorage.addFriend(44, 45);
		userStorage.deleteFriend(44, 45);
		Set<Long> friends = userStorage.getFriends(44);
		assertTrue(friends.isEmpty());
	}

}
