package org.example;

import org.example.menu.CompanyMenu;
import org.example.menu.UserMenu;

import java.util.Scanner;

/**
 * Маркетплейс
 * В приложение мложно войти от лица компании и пользователя
 * От этого зависит функционал
 * Представлена роабота с заказами, ячейками, складами и т.д.
 *
 * p.s. в проекте не очень качесвенная валидация и работа с исключеняими
 * а так я очень старался...
 *
 * author: Москаленко Дмитрий 11-401
 */
public class MarketPlaceApplication {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n____Маркетплейс____");
            System.out.println("1. Я покупатель");
            System.out.println("2. Я представитель компании");
            System.out.println("0. Выход");

            System.out.print("Выберите роль: ");
            int role = scanner.nextInt();
            scanner.nextLine();

            switch (role) {
                case 1:
                    new UserMenu().showMenu();
                    break;
                case 2:
                    new CompanyMenu().showMenu();
                    break;
                case 0:
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный ввод!");
            }
        }
    }
}