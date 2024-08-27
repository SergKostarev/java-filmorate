package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ParameterNotValidException extends IllegalArgumentException {

    private final String parameter;

    public ParameterNotValidException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
    }
}
