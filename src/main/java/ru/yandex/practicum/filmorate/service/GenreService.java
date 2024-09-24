package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final FilmStorage filmStorage;

    public Genre findGenre(Long id) {
        return filmStorage.getGenre(id);
    }

    public Collection<Genre> findAllGenres() {
        return filmStorage.getAllGenres();
    }
}
