package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("dbFilmStorage")
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
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genre";
    private static final String FIND_ALL_RATINGS_QUERY = "SELECT * FROM rating";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_RATING_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";

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
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film findFilm(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Film create(Film film) {

        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId()
        );
        return film;
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
            throw new NotFoundException(String.valueOf(id), e.getMessage());
        }
    }

    public void addFilmsGenres(long id, Set<Long> genreIds) {
        findOne(FIND_BY_ID_QUERY, id);
        for (Long genreId: genreIds) {
            getGenre(genreId);
        }
        for (Long genreId: genreIds) {
            jdbc.update(ADD_FILM_GENRE_QUERY, id, genreId);
        }
    }
}
