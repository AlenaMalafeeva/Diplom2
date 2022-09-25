package ru.yandex.praktikum.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.clients.UserClient;
import ru.yandex.praktikum.stellarburgers.api.dto.User;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class ChangeUserDataTest {
    private User user;
    private UserClient userClient;
    private String token;

    @Before
    public void init() {
        user = User.getRandomUser();
        userClient = new UserClient();
        token = userClient
                .createUser(user)
                .then()
                .extract()
                .path("accessToken");
    }

    @After
    public void tearDown() {
        if(token != null)
            userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Изменение email с авторизацией")
    @Description("Изменение данных пользователя. Проверяем, что поле email можно изменить.")
    public void checkUserEmailChange() {
        user.setEmail("new" + user.getEmail());
        Response response = userClient.updateUser(user, token);
        boolean isSuccess = response.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("success if false", isSuccess);

        String newEmail = response.then()
                .extract()
                .path("user.email");
        assertEquals("Email was not changed", user.getEmail(), newEmail);
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    @Description("Изменение данных пользователя. Проверяем, что поле password можно изменить.")
    public void checkUserPasswordChange() {
        user.setPassword(user.getPassword() + "1");
        Response response = userClient.updateUser(user, token);
        boolean isSuccess = response.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("success if false", isSuccess);
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    @Description("Изменение данных пользователя. Проверяем, что поле name можно изменить.")
    public void checkUserNameChange() {
        user.setName("new" + user.getName());
        Response response = userClient.updateUser(user, token);
        boolean isSuccess = response.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("success if false", isSuccess);

        String newName = response.then()
                .extract()
                .path("user.name");
        assertEquals("Name was not changed", user.getName(), newName);
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    @Description("Проверяем, что без авторизации нельзя внести изменения. Система должна вернуть ошибку.")
    public void checkUserDataChangeWithoutAuth() {
        user.setEmail("new" + user.getEmail());
        user.setPassword(user.getPassword() + "1");
        user.setName("new" + user.getName());

        Response response = userClient.updateUser(user);
        boolean isSuccess = response.then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .path("success");
        assertFalse("success if true", isSuccess);

        String errorMessage = response.then()
                .extract()
                .path("message");

        assertEquals("Message is incorrect", "You should be authorised", errorMessage);
    }

    @Test
    @DisplayName("Изменение email на существующий")
    @Description("Проверяем, если передать почту, которая уже используется, вернётся ошибка.")
    public void checkChangeEmailToAnExistingOne() {
        User secondUser = User.getRandomUser();
        String secondUserToken = userClient
                .createUser(secondUser)
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("accessToken");

        user.setEmail(secondUser.getEmail());
        Response response = userClient.updateUser(user, token);
        boolean isSuccess = response.then()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .path("success");
        assertFalse("success if true", isSuccess);

        String errorMessage = response.then()
                .extract()
                .path("message");

        userClient.deleteUser(secondUserToken);

        assertEquals("Message is incorrect", "User with such email already exists", errorMessage);
    }
}
