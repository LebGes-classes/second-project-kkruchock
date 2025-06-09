package org.example.repository;

import org.example.exception.RepositoryException;
import org.example.model.Warehouse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WarehouseRepository {

    private static final String FILE_PATH = "src/main/resources/Warehouse";
    private static final String HEADER = "id|address|cells quantity";

    //чтение файла
    public List<Warehouse> getAllWarehouse() throws RepositoryException {

        List<Warehouse> warehouses = new ArrayList<>();

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
                if (fields.length == 3) {
                    int id = Integer.parseInt(fields[0].trim());
                    String address = fields[1].trim();
                    int cellsQuantity = Integer.parseInt(fields[2].trim());

                    warehouses.add(new Warehouse(id, address, cellsQuantity));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл со складами не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла со складами: " + FILE_PATH);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Ошибка формата данных в файле: " + FILE_PATH);
        }
        return warehouses;
    }

    //открытие нового склада
    public void openWarehouse(Warehouse warehouse) throws RepositoryException {

        List<Warehouse> existingWarehouses = getAllWarehouse();
        for (Warehouse existing : existingWarehouses) {
            if (existing.getId() == warehouse.getId()) {
                throw new RepositoryException("Склад с ID " + warehouse.getId() + " уже существует");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String line = String.join("|",
                    String.valueOf(warehouse.getId()),
                    warehouse.getAddress(),
                    String.valueOf(warehouse.getCellsQuantity()));

            bw.write(line);
            bw.newLine();

        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл со складами не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка записи в файл со складами: " + FILE_PATH);
        }
    }

    //закрытие склада
    public void closeWarehouse(int id) throws RepositoryException {

        List<Warehouse> warehouses = getAllWarehouse();
        boolean isFound = false;

        //ищем нужный склад и удаляем
        Iterator<Warehouse> iterator = warehouses.iterator();
        while (iterator.hasNext()) {
            Warehouse warehouse = iterator.next();
            if (warehouse.getId() == id) {
                iterator.remove();
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            throw new RepositoryException("Склад с ID " + id + " не найден");
        }

        //перезаписываем файл
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            bw.write(HEADER);
            bw.newLine();

            for (Warehouse warehouse : warehouses) {
                String line = String.join("|",
                        String.valueOf(warehouse.getId()),
                        warehouse.getAddress(),
                        String.valueOf(warehouse.getCellsQuantity()));

                bw.write(line);
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл со складами не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при обновлении файла со складами: " + FILE_PATH);
        }
    }
}