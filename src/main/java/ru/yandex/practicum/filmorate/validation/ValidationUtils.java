package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;

@Slf4j
@UtilityClass
public class ValidationUtils {

    public static void idValidityCheck(Long id, String message) {
        if (id == null || id < 1) {
            String value = String.valueOf(id);
            log.error(value, message);
            throw new ParameterNotValidException(String.valueOf(id), message);
        }
    }

    public static void idInequalityCheck(long id, long friendId, String message) {
        if (id == friendId) {
            String value = String.valueOf(id);
            log.error(value, message);
            throw new ParameterNotValidException(value, message);
        }
    }
}
