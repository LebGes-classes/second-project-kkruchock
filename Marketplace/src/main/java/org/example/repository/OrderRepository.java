package org.example.repository;

import org.example.enumClass.OrderStatus;
import org.example.exception.RepositoryException;
import org.example.model.Order;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private static final String FILE_PATH = "src/main/resources/Order";
    private static final String HEADER = "id|productId|quantity|status|date|OPPid";

    //получение всех заказов
    public List<Order> getAllOrders() throws RepositoryException {
        List<Order> orders = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = false;

            while ((line = br.readLine()) != null) {
                if (!isHeader && line.equals(HEADER)) {
                    isHeader = true;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 6) {
                    Order order = parseOrder(fields);
                    orders.add(order);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла заказов: " + FILE_PATH);
        }

        return orders;
    }

    //парсинг полей заказа
    private Order parseOrder(String[] fields) {
        return new Order(
        Integer.parseInt(fields[0].trim()),
        Integer.parseInt(fields[1].trim()),
        Integer.parseInt(fields[2].trim()),
        OrderStatus.fromTitle(fields[3].trim()),
        LocalDate.parse(fields[4].trim()),
        Integer.parseInt(fields[5].trim())
        );
    }

    //получение готовых к получению =) заказов
    public List<Order> getOppOrders() throws RepositoryException {
        return getOrdersByStatus(OrderStatus.OPP);
    }

    //получение закрытых заказов
    public List<Order> getIssuedOrders() throws RepositoryException {
        return getOrdersByStatus(OrderStatus.ISSUED);
    }

    // Получение возвратов заказов
    public List<Order> getAcceptedBackOrders() throws RepositoryException {
        return getOrdersByStatus(OrderStatus.ACCEPTED_BACK);
    }

    //получение заказов по статусу
    private List<Order> getOrdersByStatus(OrderStatus status) throws RepositoryException {

        List<Order> allOrders = getAllOrders();
        List<Order> result = new ArrayList<>();

        for (Order order : allOrders) {
            if (order.getOrderStatus() == status) {
                result.add(order);
            }
        }

        return result;
    }

    //новый заказа
    public void makeOrder(Order order) throws RepositoryException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String line = String.join("|",
                    String.valueOf(order.getId()),
                    String.valueOf(order.getProductId()),
                    String.valueOf(order.getQuantity()),
                    order.getOrderStatus().getTitle(),
                    order.getDate().toString(),
                    String.valueOf(order.getOppId())
            );

            bw.write(line);
            bw.newLine();

        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при создании заказа: " +  FILE_PATH);
        }
    }

    //обновляем статус заказа
    public void updateOrderStatus(int id, OrderStatus newStatus) throws RepositoryException {

        List<Order> orders = getAllOrders();
        boolean found = false;

        for (Order order : orders) {
            if (order.getId() == id) {
                order.setOrderStatus(newStatus);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RepositoryException("Заказ с ID " + id + " не найден");
        }

        //перезаписываем файл
        rewriteFile(orders);
    }

    //перезапись
    private void rewriteFile(List<Order> orders) throws RepositoryException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            bw.write(HEADER);
            bw.newLine();

            for (Order order : orders) {
                String line = String.join("|",
                        String.valueOf(order.getId()),
                        String.valueOf(order.getProductId()),
                        String.valueOf(order.getQuantity()),
                        order.getOrderStatus().getTitle(),
                        order.getDate().toString(),
                        String.valueOf(order.getOppId())
                );

                bw.write(line);
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при обновлении файла заказов: " + FILE_PATH);
        }
    }

    //возвращаем все заказы, если пвз закрыли
    public void updateOrdersForCloseOpp(int oppId) throws RepositoryException {

        List<Order> allOrders = getAllOrders();
        boolean changed = false;

        for (Order order : allOrders) {
            if (order.getOppId() == oppId && order.getOrderStatus() == OrderStatus.OPP) {
                //меняем статус
                order.setOrderStatus(OrderStatus.ACCEPTED_BACK);

                //возвращаем товар на склад
                CellRepository cellRepository = new CellRepository();
                cellRepository.putProduct(order);

                changed = true;
            }
        }
        if (changed) {
            //если были изменения - перезапись
            rewriteFile(allOrders);
        }
    }
}