package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String identifier;

    public NotFoundException(String identifier, String message) {
        super(message);
        this.identifier = identifier;
    }
}
