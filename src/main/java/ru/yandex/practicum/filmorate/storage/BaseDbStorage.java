package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BaseDbStorage<T> {

    protected final JdbcTemplate jdbc;

    protected final RowMapper<T> mapper;

    protected T findOne(String query, Object... params) {
        try {
            return jdbc.queryForObject(query, mapper, params);
        } catch (EmptyResultDataAccessException e) {
            String id = Arrays.stream(params).toArray()[0].toString();
            log.error(id, "Не удалось сохранить данные");
            throw new NotFoundException(id, e.getMessage());
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    for (int idx = 0; idx < params.length; idx++) {
                        ps.setObject(idx + 1, params[idx]);
                    }
                    return ps;
                },
                keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            log.error(String.valueOf(id), "Не удалось сохранить данные");
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
