package ru.yandex.praktikum.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.clients.OrderClient;
import ru.yandex.praktikum.stellarburgers.api.clients.UserClient;
import ru.yandex.praktikum.stellarburgers.api.dto.Ingredients;
import ru.yandex.praktikum.stellarburgers.api.dto.Order;
import ru.yandex.praktikum.stellarburgers.api.dto.User;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CreateOrderTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String token;

    @Before
    public void init() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandomUser();
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
    @DisplayName("Создание заказа")
    @Description("Проверяем возможность сделать заказ с корректными параметрами с авторизацией.")
    public void checkOrderCreateWithIngredients() {
        Order order = new Order(Ingredients.getIngredients());
        Response response = orderClient.createOrder(order, token);
        boolean isSuccess = response.then()
                .statusCode(SC_OK)
                .extract()
                .path("success");
        assertTrue("success if false", isSuccess);
        String orderName = response.then()
                .extract()
                .path("name");
        System.out.println(orderName);
        assertThat("name can't be null", orderName, notNullValue());
    }

    @Test
    @Description("Создание заказа c некорректными параметрами")
    @DisplayName("Проверяем возможность сделать заказ c некорректными параметрами с авторизацией")
    public void checkOrderCreateWithIncorrectIngredients() {
        Order order = new Order(new String[]{Ingredients.getIngredients()[0], Ingredients.getIngredients()[2]+"INCORRECT"});
        Response response = orderClient.createOrder(order, token);
        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    @DisplayName("Проверяем возможность сделать заказ c пустыми параметрами с авторизацией")
    public void checkOrderCreateWithoutIngredients() {
        Order order = new Order(new String[]{});
        Response response = orderClient.createOrder(order, token);
        boolean isSuccess = response.then()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .path("success");
        assertFalse("success is true", isSuccess);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверяем, что только авторизованные пользователи могут делать заказы.")
    public void checkOrderCreateWithIngredientsWithoutAuth() {
        Order order = new Order(Ingredients.getIngredients());
        Response response = orderClient.createOrder(order);
        response.then().assertThat().statusCode(SC_UNAUTHORIZED);
    }

}
