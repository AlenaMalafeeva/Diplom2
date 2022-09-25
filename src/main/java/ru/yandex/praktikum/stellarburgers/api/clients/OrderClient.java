package ru.yandex.praktikum.stellarburgers.api.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.stellarburgers.api.dto.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient{
    private static final String ORDER_PATH = "/api/orders";

    @Step("Запрос на создание заказа {order}")
    public Response createOrder(Order order, String token) {
        return given()
                .spec(getReqSpec())
                .header("Authorization", token)
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Запрос на создание заказа {order} буз токена")
    public Response createOrder(Order order) {
        return given()
                .spec(getReqSpec())
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Запрос на получение заказов пользователя")
    public Response getUserOrders(String token) {
        return given()
                .spec(getReqSpec())
                .header("Authorization", token)
                .when()
                .get(ORDER_PATH);
    }

    @Step("Запрос на получение заказов пользователя без токена")
    public Response getUserOrders() {
        return given()
                .spec(getReqSpec())
                .when()
                .get(ORDER_PATH);
    }
}
