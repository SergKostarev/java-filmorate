package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("dbFilmStorage")
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Rating> ratingMapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ? WHERE id = ?";
    private static final String GET_LIKES_ID_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT into likes(user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE from likes WHERE user_id = ? AND film_id = ?";
    private static final String GET_MOST_POPULAR_QUERY = "SELECT * FROM films WHERE id IN (" +
            "SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?)";
    private static final String ADD_FILM_GENRE_QUERY = "INSERT into film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE from film_genre WHERE film_id = ?";
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genre";
    private static final String FIND_ALL_RATINGS_QUERY = "SELECT * FROM rating";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_RATING_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";
    private static final String GET_FILM_GENRES = "SELECT g.id, g.description FROM film_genre fg " +
            "JOIN genre AS g ON fg.genre_id = g.id WHERE fg.film_id = ?";

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
    public Collection<Film> findAll() {
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

    @Override
    public Film create(Film film) { // TODO use parent class method
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1, film.getName());
                    ps.setObject(2, film.getDescription());
                    ps.setObject(3, film.getReleaseDate());
                    ps.setObject(4, film.getDuration());
                    /*
                    if (film.getMpa() == null) {
                        ps.setNull(5, Types.BIGINT);
                    } else {
                        ps.setObject(5, film.getMpa().getId());
                    }
                    */
                    ps.setObject(5, film.getMpa() == null ? null : film.getMpa().getId());
                    return ps;
                },
                keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            film.setId(id);
            for (Genre g: film.getGenres()) {
                jdbc.update(ADD_FILM_GENRE_QUERY, id, g.getId());
            }
            return findFilm(id);
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Film update(Film film) { // TODO use parent class method
        jdbc.update(connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1, film.getName());
                    ps.setObject(2, film.getDescription());
                    ps.setObject(3, film.getReleaseDate());
                    ps.setObject(4, film.getDuration());
                    /*
                    if (film.getMpa() == null) {
                        ps.setNull(5, Types.BIGINT);
                    } else {
                        ps.setObject(5, film.getMpa().getId());
                    }
                    */
                    ps.setObject(5, film.getMpa() == null ? null : film.getMpa().getId());
                    ps.setObject(6, film.getId());
                    return ps;
                }
                );
        jdbc.update(DELETE_FILM_GENRE_QUERY, film.getId());
        for (Genre g: film.getGenres()) {
            jdbc.update(ADD_FILM_GENRE_QUERY, film.getId(), g.getId());
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
            throw new NotFoundException(String.valueOf(id), "Жанр не найден");
        }
    }

    private List<Genre> getFilmsGenres(long filmId) {
        return jdbc.query(GET_FILM_GENRES, genreMapper, filmId);
    }

    private void fillGenresAndRating(Film film) {
        film.setGenres(getFilmsGenres(film.getId()));
        if (film.getMpa() != null) {
            film.setMpa(getRating(film.getId()));
        }
    }

}
