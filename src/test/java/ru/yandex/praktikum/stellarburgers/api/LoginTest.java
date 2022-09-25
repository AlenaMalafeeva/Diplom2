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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class LoginTest {
    private User user = User.getRandomUser();
    private UserClient userClient;
    private String token;

    @Before
    public void init() {
        user = User.getRandomUser();
        userClient = new UserClient();
        token = userClient.createUser(user).then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if(token != null) {
            userClient.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверяем возможность авторизоваться с правильной парой логин-пароль.")
    public void checkLogInWithCorrectData() {
        Response response = userClient.login(UserCredentials.from(user));
        boolean isLoginSuccessful = response.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("success is false, but it should be true", isLoginSuccessful);

        String bearerToken = response.then()
                .extract()
                .path("accessToken");
        assertNotNull("accessToken can't be null", bearerToken);

        String refreshToken = response.then()
                .extract()
                .path("refreshToken");
        assertNotNull("refreshToken can't be null", refreshToken);

        String userEmail = response.then()
                .extract()
                .path("user.email");
        assertEquals("incorrect user email", user.getEmail(), userEmail);

        String userName = response.then()
                .extract()
                .path("user.name");
        assertEquals("incorrect user name", user.getName(), userName);
    }

    @Test
    @DisplayName("Логин с неправильным паролем")
    @Description("Проверяем, что нельзя авторизоваться, указав неправильный пароль.")
    public void checkLogInWithIncorrectPassword() {
        UserCredentials userCredentials = UserCredentials.from(user);
        userCredentials.setPassword(userCredentials.getPassword()+"12");

        Response logInResponse = userClient.login(userCredentials);
        boolean isLoginSuccessful = logInResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .path("success");
        assertFalse("Success login with incorrect password", isLoginSuccessful);
    }

    @Test
    @DisplayName("Логин с неправильным логином")
    @Description("Проверяем, что нельзя авторизоваться, указав неправильный логин.")
    public void checkLogInWithIncorrectLogin() {
        UserCredentials userCredentials = UserCredentials.from(user);
        userCredentials.setEmail("01" + userCredentials.getEmail());

        Response logInResponse = userClient.login(userCredentials);
        boolean isLoginSuccessful = logInResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .path("success");
        assertFalse("Success login with incorrect email", isLoginSuccessful);
    }

    @Test
    @DisplayName("Логин под несуществующим пользователем")
    @Description("Проверяем, что логин с неправильным логином и паролем невозможен.")
    public void checkLogInWithNonExistingCourier() {
        UserCredentials userCredentials = UserCredentials.from(user);
        userCredentials.setPassword(userCredentials.getPassword()+"12");
        userCredentials.setEmail("01" + userCredentials.getEmail());

        Response logInResponse = userClient.login(userCredentials);
        boolean isLoginSuccessful = logInResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .path("success");
        assertFalse("Success login with incorrect email and password", isLoginSuccessful);
    }
}
