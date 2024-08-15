package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerTest {

    UserController userController;
    Validator validator;

    @BeforeEach
    public void getController() {
        userController = new UserController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void givenCorrectUser_shouldCreateUser() {
        User user = new User(null, "example@example.com", "Test_login",
                "Test name", LocalDate.of(2000, 1, 1));
        User userReturned = userController.create(user);
        Assertions.assertNotNull(userReturned);
        Assertions.assertEquals(1, userController.findAll().size());
    }

    @Test
    public void givenIncorrectUser_shouldNotCreateUser() {
        User user = new User(null, "example@example.com", "",
                "Test name", LocalDate.of(2000, 1, 1));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void givenIncorrectUserEmail_shouldNotCreateUser() {
        User user = new User(null, ".com@", "Test_login",
                "Test name", LocalDate.of(2000, 1, 1));
        assertThat(validator.validate(user).size()).isEqualTo(1);
    }

    @Test
    public void givenCorrectUser_shouldUpdateUser() {
        User user = new User(null, "example@example.com", "Test_login",
                "Test name", LocalDate.of(2000, 1, 1));
        User userReturned = userController.create(user);
        Assertions.assertNotNull(userReturned);
        User userUpdated = userController.update(new User(1L, "example@example.com",
                "Test_login_2", "Test name 2", LocalDate.of(2000, 1, 1)));
        Assertions.assertNotNull(userUpdated);
        Assertions.assertEquals(user.getName(), "Test name 2");
        Assertions.assertEquals(user.getLogin(), "Test_login_2");
    }

    @Test
    public void givenIncorrectUser_shouldNotUpdateUser() {
        User userReturned = userController.create(new User(null, "example@example.com",
                "Test_login", "Test name", LocalDate.of(2000, 1, 1)));
        Assertions.assertNotNull(userReturned);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(new User(null, "example_example.com",
                "", "Test name", LocalDate.of(2000, 1, 1))));
    }

    @Test
    public void givenCorrectUsers_shouldReturnUserCollection() {
        User userReturned = userController.create(new User(null, "example@example.com",
                "Test_login", "Test name", LocalDate.of(2000, 1, 1)));
        User userReturned2 = userController.create(new User(null, "example@example.com",
                "Test_login_2", "Test name 2", LocalDate.of(2000, 1, 1)));
        Assertions.assertNotNull(userReturned);
        Assertions.assertNotNull(userReturned2);
        Assertions.assertEquals(2, userController.findAll().size());
    }
}
