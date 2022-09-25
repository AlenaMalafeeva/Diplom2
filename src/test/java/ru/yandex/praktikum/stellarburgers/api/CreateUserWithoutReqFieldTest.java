package ru.yandex.praktikum.stellarburgers.api;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.stellarburgers.api.clients.UserClient;
import ru.yandex.praktikum.stellarburgers.api.dto.User;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class CreateUserWithoutReqFieldTest {
    private static final Faker faker = new Faker();
    private final String email;
    private final String password;
    private final String name;

    public CreateUserWithoutReqFieldTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "test data: {0}, {1}, {2}")
    public static Object[][] getData() {
        return new Object[][]{
                {null, null, null},
                {faker.internet().emailAddress(), faker.bothify("###??#?#?##??"), null},
                {faker.internet().emailAddress(), null, faker.name().fullName()},
                {null, faker.bothify("###??#?#?##??"), faker.name().fullName()},
                {"", "", ""},
        };
    }

    @Test
    public void checkUserCreationWithoutRequiredFields() {
        User user = new User(email, password, name);
        UserClient userClient = new UserClient();

        Response createUserResponse = userClient.createUser(user);
        boolean isUserCreated = createUserResponse.then()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .path("success");
        assertFalse("User is created without required fields", isUserCreated);
    }
}
