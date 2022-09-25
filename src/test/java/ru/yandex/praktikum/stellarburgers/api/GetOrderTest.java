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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.*;

public class GetOrderTest {
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
    @DisplayName("Получение списка заказов")
    @Description("Получение списка заказов пользователя с авторизацией")
    public void checkOrdersGettingWithAuth(){
        Order order = new Order(Ingredients.getIngredients());
        orderClient.createOrder(order, token);

        order = new Order(Ingredients.getIngredients());
        orderClient.createOrder(order, token);

        Response response = orderClient.getUserOrders(token);
        boolean isSuccess = response.then()
                .statusCode(SC_OK)
                .assertThat().body("$", hasKey("orders"))
                .extract()
                .path("success");

        assertTrue("success is false", isSuccess);

        int total = response.then()
                .extract()
                .path("total");
        assertEquals("total number of orders is incorrect", 2, total);

        int totalToday = response.then()
                .extract()
                .path("totalToday");
        assertEquals("totalToday number of orders is incorrect", 2, totalToday);
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации")
    @Description("Получение списка заказов неавторизованным пользователем")
    public void checkOrdersGettingWithoutAuth(){
        Order order = new Order(Ingredients.getIngredients());
        orderClient.createOrder(order, token);

        order = new Order(Ingredients.getIngredients());
        orderClient.createOrder(order, token);

        Response response = orderClient.getUserOrders();

        boolean isSuccess = response.then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .path("success");

        assertFalse("success is true", isSuccess);

        String errorMessage = response.then()
                .extract()
                .path("message");
        assertEquals("The message is incorrect", "You should be authorised", errorMessage);
    }
}
