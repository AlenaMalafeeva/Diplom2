package ru.yandex.praktikum.stellarburgers.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString(includeFieldNames = false)
public class Order {
    private String[] ingredients;
}
