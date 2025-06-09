package org.example.menu;

import org.example.enumClass.OrderStatus;
import org.example.exception.RepositoryException;
import org.example.service.*;
import org.example.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class UserMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService = new UserService();

    //меню юзера
    public void showMenu() {
        while (true) {
            System.out.println("\n____Меню покупателя____");
            System.out.println("1. Просмотреть все товары");
            System.out.println("2. Оформить заказ");
            System.out.println("3. Просмотреть заказы (готовые к получению)");
            System.out.println("4. Получить заказ");
            System.out.println("5. Вернуть заказ");
            System.out.println("0. Выход");

            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        showAllProducts();
                        break;
                    case 2:
                        makeOrder();
                        break;
                    case 3:
                        showOppOrders();
                        break;
                    case 4:
                        receiveOrder();
                        break;
                    case 5:
                        returnOrder();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Неверный ввод!");
                        break;
                }
            } catch (RepositoryException e) {
                System.out.println("Ошибка репозитория");
            }
        }
    }

    //cписок
    private void showAllProducts() throws RepositoryException {
        System.out.println("\n____Список товаров____");
        HashMap<Integer, Integer> productQuantities = userService.getAllProductsWithQuantity();
        List<Product> products = userService.getAllProducts();

        for (Product product : products) {
            int quantity = productQuantities.getOrDefault(product.getId(), 0);
            System.out.printf("ID: %d | Название: %s | Цена: %.2f | В наличии: %d%n",
                    product.getId(), product.getName(), product.getPrice(), quantity);
        }
    }

    //заказ
    private void makeOrder() throws RepositoryException {

        System.out.print("Введите ID товара: ");
        int productId = scanner.nextInt();
        System.out.print("Введите количество: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите ID ПВЗ: ");
        int oppId = scanner.nextInt();
        scanner.nextLine();

        Order order = new Order(
                generateOrderId(),
                productId,
                quantity,
                OrderStatus.OPP,
                java.time.LocalDate.now(),
                oppId
        );

        userService.makeOrder(order);
        System.out.println("Заказ оформлен!");
    }

    //посмотреть готовые
    private void showOppOrders() throws RepositoryException {
        System.out.println("\n____Заказы готовые к получению____");
        List<Order> orders = userService.getOppOrders();
        for (Order order : orders) {
            System.out.printf("ID: %d | Товар: %d | Количество: %d | ПВЗ: %d%n",
                    order.getId(), order.getProductId(), order.getQuantity(), order.getOppId());
        }
    }

    //забрать заказ
    private void receiveOrder() throws RepositoryException {
        System.out.print("Введите ID заказа: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();
        userService.receiveOrder(orderId);
        System.out.println("Заказ получен!");
    }

    //вернуть
    private void returnOrder() throws RepositoryException {
        System.out.print("Введите ID заказа: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();
        userService.returnOrder(orderId);
        System.out.println("Заказ возвращен!");
    }

    //костыли, не доделал в репозитории
    private int generateOrderId() {
        return new Random().nextInt(1000) + 1;
    }
}