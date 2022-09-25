package ru.yandex.praktikum.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.clients.UserClient;
import ru.yandex.praktikum.stellarburgers.api.dto.User;
import ru.yandex.praktikum.stellarburgers.api.dto.UserCredentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateUserTest {
    private User user;
    private UserClient userClient;
    private String token;

    @Before
    public void init() {
        user = User.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if(token != null)
            userClient.deleteUser(token);
    }

    @DisplayName("Создание уникального пользователя")
    @Description("Проверка регистрации и авторизации нового пользователя с корректными данными.")
    @Test
    public void checkUserCreationWithValidData() {
        Response createUserResponse = userClient.createUser(user);
        boolean isUserCreated = createUserResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("Courier isn't created", isUserCreated);

        token = createUserResponse.then()
                .extract()
                .path("accessToken");

        Response loginResponse = userClient.login(UserCredentials.from(user));
        boolean isLoginSuccessful = loginResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("Login failed", isLoginSuccessful);
    }

    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверяем, что нельзя создать двух одинаковых пользователей.")
    @Test
    public void checkCreateDuplicateUser() {
        Response createUserResponse = userClient.createUser(user);
        boolean isUserCreated = createUserResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("Courier isn't created", isUserCreated);

        token = createUserResponse.then()
                .extract()
                .path("accessToken");

        Response loginResponse = userClient.login(UserCredentials.from(user));
        boolean isLoginSuccessful = loginResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("Login failed", isLoginSuccessful);

        int responseStatusCodeDuplicateUser = userClient.createUser(user).statusCode();
        assertEquals(SC_FORBIDDEN, responseStatusCodeDuplicateUser);
    }
}
