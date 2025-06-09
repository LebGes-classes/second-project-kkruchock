package org.example.enumClass;

import java.util.Arrays;

public enum OrderStatus {

    OPP ("В пункте выдаче заказов"),
    ISSUED ("Уже у вас"),
    ACCEPTED_BACK ("Принят на возврат");

    private String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Статус заказа: " + title;
    }

    public static OrderStatus fromTitle(String title) {
        return Arrays.stream(values())
                .filter(status -> status.title.equalsIgnoreCase(title.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + title));
    }
}