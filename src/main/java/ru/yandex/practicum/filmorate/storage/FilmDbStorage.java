package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("dbFilmStorage")
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Rating> ratingMapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE id = ?";
    private static final String GET_LIKES_ID_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT into likes(user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE from likes WHERE user_id = ? AND film_id = ?";
    private static final String GET_MOST_POPULAR_QUERY = "SELECT id, name, description, release_date, duration " +
            "FROM films JOIN likes AS l ON l.film_id = films.id GROUP BY (id) ORDER BY (COUNT(id)) DESC LIMIT ?";
    private static final String ADD_FILM_GENRE_QUERY = "INSERT into film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE from film_genre WHERE film_id = ?";
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genre";
    private static final String FIND_ALL_RATINGS_QUERY = "SELECT * FROM rating";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_RATING_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";
    private static final String GET_FILM_GENRES = "SELECT g.id, g.description FROM film_genre fg " +
            "JOIN genre AS g ON fg.genre_id = g.id WHERE fg.film_id = ?";
    private static final String ADD_FILM_RATING_QUERY = "INSERT into film_rating(film_id, rating_id) VALUES (?, ?)";
    private static final String DELETE_FILM_RATING_QUERY = "DELETE from film_rating WHERE film_id = ?";
    private static final String GET_FILM_RATING = "SELECT r.id, r.description FROM film_rating fr " +
            "JOIN rating AS r ON fr.rating_id = r.id WHERE fr.film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, RowMapper<Genre> genreMapper, RowMapper<Rating> ratingMapper) {
        super(jdbc, mapper);
        this.genreMapper = genreMapper;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public Set<Long> getLikes(long id) {
        return Set.copyOf((jdbc.query(GET_LIKES_ID_QUERY, new SingleColumnRowMapper<>(Long.class), id)));
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        for (Film film: films) {
            fillGenresAndRating(film);
        }
        return films;
    }

    @Override
    public Film findFilm(long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id);
        fillGenresAndRating(film);
        return film;
    }

    private void addFilmGenres(Film film) {
        List<Genre> genres = film.getGenres();
        jdbc.batchUpdate(ADD_FILM_GENRE_QUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        film.setId(id);
        addFilmGenres(film);
        if (film.getMpa() != null) {
            jdbc.update(ADD_FILM_RATING_QUERY, id, film.getMpa().getId());
        }
        return findFilm(id);
    }

    @Override
    public Film update(Film film) {
        findFilm(film.getId());
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
        jdbc.update(DELETE_FILM_GENRE_QUERY, film.getId());
        jdbc.update(DELETE_FILM_RATING_QUERY, film.getId());
        addFilmGenres(film);
        if (film.getMpa() != null) {
            jdbc.update(ADD_FILM_RATING_QUERY, film.getId(), film.getMpa().getId());
        }
        return findFilm(film.getId());
    }

    @Override
    public void dislike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, userId, filmId);
    }

    @Override
    public void like(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return jdbc.query(GET_MOST_POPULAR_QUERY, mapper, count);
    }

    @Override
    public Collection<Rating> getAllRatings() {
        return jdbc.query(FIND_ALL_RATINGS_QUERY, ratingMapper);
    }

    @Override
    public Rating getRating(long id) {
        try {
            return jdbc.queryForObject(FIND_RATING_BY_ID_QUERY, ratingMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.error(String.valueOf(id), "Рейтинг не найден");
            throw new NotFoundException(String.valueOf(id), "Рейтинг не найден");
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbc.query(FIND_ALL_GENRES_QUERY, genreMapper);
    }

    @Override
    public Genre getGenre(long id) {
        try {
            return jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, genreMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.error(String.valueOf(id), "Жанр не найден");
            throw new NotFoundException(String.valueOf(id), "Жанр не найден");
        }
    }

    private List<Genre> getFilmsGenres(long filmId) {
        return jdbc.query(GET_FILM_GENRES, genreMapper, filmId);
    }

    private Optional<Rating> getFilmRating(long filmId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_FILM_RATING, ratingMapper, filmId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void fillGenresAndRating(Film film) {
        film.setGenres(getFilmsGenres(film.getId()));
        Optional<Rating> mpa = getFilmRating(film.getId());
        if (mpa.isPresent()) {
            film.setMpa(mpa.get());
        }
    }
}
