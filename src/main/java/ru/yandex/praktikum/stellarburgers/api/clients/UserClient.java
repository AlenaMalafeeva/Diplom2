package ru.yandex.praktikum.stellarburgers.api.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.stellarburgers.api.dto.User;
import ru.yandex.praktikum.stellarburgers.api.dto.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient {
    private static final String USER_CREATION_PATH = "/api/auth/register";
    private static final String USER_LOGIN_PATH = "/api/auth/login";
    private static final String USER_CHANGE_PATH = "/api/auth/user";

    @Step("Отправляем запрос на создание пользователя {user}")
    public Response createUser(User user) {
        return given()
                .spec(getReqSpec())
                .body(user)
                .when()
                .post(USER_CREATION_PATH);
    }

    @Step("Отправляем запрос на авторизацию {userCredentials}")
    public Response login(UserCredentials userCredentials) {
        return given()
                .spec(getReqSpec())
                .body(userCredentials)
                .when()
                .post(USER_LOGIN_PATH);
    }

    @Step("Отправляем запрос на удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .spec(getReqSpec())
                .header("Authorization", token)
                .when()
                .delete(USER_CHANGE_PATH);
    }

    @Step("Отправляем запрос без токена на обновление информации о пользователе {user}")
    public Response updateUser(User user){
        return given()
                .spec(getReqSpec())
                .body(user)
                .when()
                .patch(USER_CHANGE_PATH);
    }

    @Step("Отправляем запрос на обновление информации о пользователе {user}")
    public Response updateUser(User user, String token){
        return given()
                .spec(getReqSpec())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER_CHANGE_PATH);
    }
}
