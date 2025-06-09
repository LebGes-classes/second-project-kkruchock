package org.example.menu;

import org.example.exception.RepositoryException;
import org.example.service.CompanyService;
import org.example.model.*;

import java.util.List;
import java.util.Scanner;

public class CompanyMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyService companyService = new CompanyService();

    public void showMenu() {
        while (true) {
            System.out.println("\n____Меню компании____");
            System.out.println("1. Список складов");
            System.out.println("2. Управление складами");
            System.out.println("3. Список ПВЗ");
            System.out.println("4. Управление ПВЗ");
            System.out.println("5. Список сотрудников");
            System.out.println("6. Управление сотрудниками");
            System.out.println("7. Добавить товар");
            System.out.println("8. Просмотреть пустые ячейки");
            System.out.println("0. Выход");

            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        showAllWarehouses();
                        break;
                    case 2:
                        manageWarehouses();
                        break;
                    case 3:
                        showAllOpps();
                        break;
                    case 4:
                        manageOpps();
                        break;
                    case 5:
                        showAllEmployes();
                        break;
                    case 6:
                        manageEmployees();
                        break;
                    case 7:
                        addProduct();
                        break;
                    case 8:
                        showInfo();
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

    private void showAllWarehouses() throws RepositoryException {
        System.out.println("\n____Список складов____");
        List<Warehouse> warehouses = companyService.getAllWarehouse();

        for (Warehouse warehouse : warehouses) {
            System.out.printf("ID: %d | Адресс: %s | Кол-во ячеек: %d%n",
                    warehouse.getId(), warehouse.getAddress(), warehouse.getCellsQuantity());
        }
    }

    //cклад
    private void manageWarehouses() throws RepositoryException {
        System.out.println("\n1. Открыть склад | 2. Закрыть склад");
        int action = scanner.nextInt();
        scanner.nextLine();

        if (action == 1) {
            System.out.print("Введите ID склада: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Введите адрес: ");
            String address = scanner.nextLine();
            System.out.print("Введите количество ячеек: ");
            int cells = scanner.nextInt();
            scanner.nextLine();

            companyService.openWarehouse(new Warehouse(id, address, cells));
            System.out.println("Склад открыт!");

        } else if (action == 2) {
            System.out.print("Введите ID склада: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            companyService.closeWarehouse(id);
            System.out.println("Склад закрыт!");
        }
    }

    private void showAllOpps() throws RepositoryException {
        System.out.println("\n____Список ПВЗ____");

        List<OrderPickUpPoint> pickUpPoints = companyService.getAllOops();

        for (OrderPickUpPoint point : pickUpPoints) {
            System.out.printf("ID: %d | Адрес: %s%n",
                    point.getId(), point.getAddress());
        }
    }

    //пвз
    private void manageOpps() throws RepositoryException {
        System.out.println("\n1. Открыть ПВЗ | 2. Закрыть ПВЗ");
        int action = scanner.nextInt();
        scanner.nextLine();

        if (action == 1) {
            System.out.print("Введите ID ПВЗ: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Введите адрес: ");
            String address = scanner.nextLine();

            companyService.openOrderPickUpPoint(new OrderPickUpPoint(id, address));
            System.out.println("ПВЗ открыт!");

        } else if (action == 2) {
            System.out.print("Введите ID ПВЗ: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            companyService.closeOrderPickUpPoint(id);
            System.out.println("ПВЗ закрыт!");
        }
    }

    private void showAllEmployes() throws RepositoryException {
        System.out.println("\n____Список сотдруников____");
        List<Employee> employees = companyService.getAllEmployee();

        for (Employee employee : employees) {
            System.out.printf("ID: %d | Имя: %s | ID работы: %d | Место работы: %s%n" ,
                    employee.getId(), employee.getName(), employee.getWorkPlaceId(), employee.getWorkPlaceName());
        }
    }

    //нанять уволить сотрудника
    private void manageEmployees() throws RepositoryException {
        System.out.println("\n1. Нанять сотрудника | 2. Уволить сотрудника");
        int action = scanner.nextInt();
        scanner.nextLine();

        if (action == 1) {
            System.out.print("Введите ID сотрудника: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Введите имя: ");
            String name = scanner.nextLine();
            System.out.print("Введите ID места работы: ");
            int workplaceId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Введите название места работы: ");
            String workplaceName = scanner.nextLine();

            companyService.hireEmployee(new Employee(id, name, workplaceId, workplaceName));
            System.out.println("Сотрудник нанят!");
        } else if (action == 2) {
            System.out.print("Введите ID сотрудника: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            companyService.fireEmployee(id);
            System.out.println("Сотрудник уволен!");
        }
    }

    //новый товар
    private void addProduct() throws RepositoryException {
        System.out.print("Введите ID товара: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите название: ");
        String name = scanner.nextLine();
        System.out.print("Введите цену: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        companyService.addProduct(new Product(id, name, price));
        System.out.println("Товар добавлен!");
    }

    //количество пустых ячеек
    private void showInfo() throws RepositoryException {
        System.out.print("Введите ID склада для проверки пустых ячеек: ");
        int warehouseId = scanner.nextInt();
        scanner.nextLine();
        int emptyCells = companyService.getEmptyCellsCount(warehouseId);
        System.out.println("Пустых ячеек: " + emptyCells);
    }
}