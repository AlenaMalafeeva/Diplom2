package ru.yandex.praktikum.stellarburgers.api.dto;

public class Ingredients {
    private static String[] ingredients_hash = {"61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa6c"};

    public static String[] getIngredients() {
        return ingredients_hash;
    }
}
