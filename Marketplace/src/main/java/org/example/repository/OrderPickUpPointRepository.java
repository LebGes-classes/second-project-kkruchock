package org.example.repository;

import org.example.exception.RepositoryException;
import org.example.model.OrderPickUpPoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderPickUpPointRepository {

    private static final String FILE_PATH = "src/main/resources/Opp";
    private static final String HEADER = "id|address";

    //чтение файла
    public List<OrderPickUpPoint> getAllOrderPickUpPoints() throws RepositoryException {
        
        List<OrderPickUpPoint> orderPickUpPoints = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;
            boolean isHeader = false;

            while ((line = br.readLine()) != null) {

                if (!isHeader) {
                    if (line.equals(HEADER)) {
                        isHeader = true;
                    }
                    continue;
                }

                // Пропускаем пустые строки
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 2) {
                    int id = Integer.parseInt(fields[0].trim());
                    String address = fields[1].trim();

                    orderPickUpPoints.add(new OrderPickUpPoint(id, address));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл с ПВЗ не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла с ПВЗ: " + FILE_PATH);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Ошибка формата данных в файле: " + FILE_PATH);
        }
        return orderPickUpPoints;
    }

    //открытие нового ПВЗ
    public void openOrderPickUpPoint(OrderPickUpPoint orderPickUpPoint) throws RepositoryException {
        
        List<OrderPickUpPoint> existingOrderPickUpPoints = getAllOrderPickUpPoints();
        
        for (OrderPickUpPoint existing : existingOrderPickUpPoints) {
            if (existing.getId() == orderPickUpPoint.getId()) {
                throw new RepositoryException("Склад с ID " + orderPickUpPoint.getId() + " уже существует");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String line = String.join("|",
                    String.valueOf(orderPickUpPoint.getId()),
                    orderPickUpPoint.getAddress()
            );

            bw.write(line);
            bw.newLine();

        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл с ПВЗ не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка записи в файл с ПВЗ: " + FILE_PATH);
        }
    }

    //закрытие ПВЗ
    public void closeOrderPickUpPoint(int id) throws RepositoryException {
        
        List<OrderPickUpPoint> orderPickUpPoints = getAllOrderPickUpPoints();
        boolean isFound = false;

        //ищем нужный ПВЗ и удаляем
        Iterator<OrderPickUpPoint> iterator = orderPickUpPoints.iterator();
        while (iterator.hasNext()) {
            OrderPickUpPoint orderPickUpPoint = iterator.next();
            if (orderPickUpPoint.getId() == id) {
                iterator.remove();
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            throw new RepositoryException("ПВЗ с ID " + id + " не найден");
        }

        //перезаписываем файл
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            bw.write(HEADER);
            bw.newLine();

            for (OrderPickUpPoint oderPickUpPoint : orderPickUpPoints) {
                String line = String.join("|",
                        String.valueOf(oderPickUpPoint.getId()),
                        oderPickUpPoint.getAddress()
                );

                bw.write(line);
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл c ПВЗ не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при обновлении файла с ПВЗ: " + FILE_PATH);
        }
    }
        
}