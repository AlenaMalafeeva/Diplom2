package ru.yandex.praktikum.stellarburgers.api.dto;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString(includeFieldNames = false)
public class User {
    private String email;
    private String password;
    private String name;

    public static User getRandomUser() {
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = faker.bothify("###??#?#?##??");
        String name = faker.name().fullName();

        return new User(email, password, name);
    }
}