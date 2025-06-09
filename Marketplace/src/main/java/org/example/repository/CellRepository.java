package org.example.repository;

import org.example.exception.RepositoryException;
import org.example.model.Order;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CellRepository {

    private static final String FILE_PATH = "src/main/resources/Cell";
    private static final String HEADER = "id|warehouse id|product id|product quantity";

    //узнаем общее количество товаров во всех ячейках
    public HashMap<Integer, Integer> getAllProductQuantity() throws RepositoryException {

        HashMap<Integer, Integer> productQuantityMap = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) { //читаем кусок 8 кб файл ридером и оставляем его в буфере, далее обращаемся к буфферу

            String line;
            boolean isHeader = false;

            while ((line = br.readLine()) != null) {

                //Пропускаем заголовок
                if (!isHeader && line.equals(HEADER)) {
                    isHeader = true;
                    continue;
                }

                //Пропускаем пустые строки
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 4) {
                    productQuantityMap.merge(Integer.parseInt(fields[2].trim()), Integer.parseInt(fields[3].trim()), Integer::sum); //накапливаем значение id товара: количество
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла " + FILE_PATH);
        }
        return productQuantityMap;
    }

    //обновляем ячейки после покупки, ищем перву попавшуюся и отнимаем товар, пока buyQuantity не кончится
    //наверное правильнее было бы подавать на вход Order order, как в последнем методе
    public void updateSells(int productId, int buyQuantity) throws RepositoryException {
        List<String> fileLines = new ArrayList<>();
        boolean productFound = false;
        int remainingQuantity = buyQuantity;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = false;

            while ((line = br.readLine()) != null) {
                if (!isHeader && line.equals(HEADER)) {
                    fileLines.add(line);
                    isHeader = true;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    fileLines.add(line);
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 4) {
                    int currentProductId = Integer.parseInt(fields[2].trim());
                    int currentQuantity = Integer.parseInt(fields[3].trim());

                    if (currentProductId == productId && currentQuantity > 0 && remainingQuantity > 0) {
                        productFound = true;
                        int quantityToReduce = Math.min(currentQuantity, remainingQuantity);
                        currentQuantity -= quantityToReduce;
                        remainingQuantity -= quantityToReduce;

                        if (currentQuantity == 0) {
                            line = fields[0] + "|" + fields[1] + "|0|0";
                        } else {
                            line = fields[0] + "|" + fields[1] + "|" + productId + "|" + currentQuantity;
                        }
                    }
                }
                fileLines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла: " + FILE_PATH);
        }

        if (!productFound) {
            throw new RepositoryException("Товар с ID " + productId + " не найден");
        }
        if (remainingQuantity > 0) {
            throw new RepositoryException("Недостаточно товара с ID " + productId + " для покупки");
        }

        //перезаписываем
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String updatedLine : fileLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Ошибка записи в файл: " + FILE_PATH);
        }
    }

    //узнать сколько всего пустых ячеек
    public int countEmptyCells() throws RepositoryException {

        int emptyCells = 0;

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
                if (fields.length == 4) {
                    int quantity = Integer.parseInt(fields[3].trim());
                    if (quantity == 0) {
                        emptyCells++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла: " + FILE_PATH);
        }

        return emptyCells;
    }

    //узнать сколько пустых ячеек на складе
    public int countEmptyCells(int wareHouseId) throws RepositoryException {

        int emptyCells = 0;

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
                if (fields.length == 4) {
                    int currentWareHouseId = Integer.parseInt(fields[1].trim());
                    int quantity = Integer.parseInt(fields[3].trim());
                    if (currentWareHouseId == wareHouseId && quantity == 0) {
                        emptyCells++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла: " + FILE_PATH);
        }

        return emptyCells;
    }

    //удаляем ячейки, если закрыли склад
    //товары сгорают =(
    public void deleteCellsByWarehouse(int warehouseId) throws RepositoryException {List<String> remainingCells = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;
            boolean isHeader = false;

            while ((line = br.readLine()) != null) {

                if (!isHeader && line.equals(HEADER)) {
                    remainingCells.add(line);
                    isHeader = true;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    remainingCells.add(line);
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 4) {
                    int currentWarehouseId = Integer.parseInt(fields[1].trim());
                    if (currentWarehouseId != warehouseId) {
                        remainingCells.add(line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла ячеек: " + FILE_PATH);
        }

        // Перезаписываем файл без ячеек закрытого склада
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String cell : remainingCells) {
                bw.write(cell);
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка записи в файл ячеек: " + FILE_PATH);
        }
    }

    //положить товар в ячейку
    public void putProduct(int productId, int quantity) throws RepositoryException {

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            List<String> lines = new ArrayList<>();
            String line;
            boolean isHeader = false;
            boolean productAdded = false;

            while ((line = br.readLine()) != null) {

                if (!isHeader && line.equals(HEADER)) {
                    lines.add(line);
                    isHeader = true;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    lines.add(line);
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 4) {
                    int currentQuantity = Integer.parseInt(fields[3].trim());

                    //если нашли пустую
                    if (currentQuantity == 0) {
                        line = fields[0] + "|" + fields[1] + "|" +
                                productId + "|" + quantity;
                        productAdded = true;
                    }
                }
                lines.add(line);
            }

            //если не нашли пустую ячейку
            if (!productAdded) {
                System.out.println("Не можем приянть новый товар, нет места на складах");
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при возврате товара: " + FILE_PATH);
        }
    }

    //создание пустых ячеек при создании склада
    public void createNewEmptyCells(int wareHouseId, int cellQuantity) throws RepositoryException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            int startId = findMaxCellId() + 1;

            for (int i = 0; i < cellQuantity; i++) {
                String cellLine = (startId + i) + "|" + wareHouseId + "|0|0";
                bw.write(cellLine);
                bw.newLine();
            }

        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при создании ячеек: " + FILE_PATH);
        }
    }

    //вспомогательный метод для создания ячеек
    private int findMaxCellId() throws RepositoryException {
        int maxId = 0;
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
                if (fields.length == 4) {
                    int currentId = Integer.parseInt(fields[0].trim());
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при поиске максимального ID ячейки");
        }
        return maxId;
    }

    //при возврате заказа ищем пустую ячейку и кладем в нее
    public void putProduct(Order order) throws RepositoryException {

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            List<String> lines = new ArrayList<>();
            String line;
            boolean isHeader = false;
            boolean productAdded = false;

            while ((line = br.readLine()) != null) {

                if (!isHeader && line.equals(HEADER)) {
                    lines.add(line);
                    isHeader = true;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    lines.add(line);
                    continue;
                }

                String[] fields = line.split("\\|");
                if (fields.length == 4) {
                    int currentQuantity = Integer.parseInt(fields[3].trim());

                    //если нашли пустую
                    if (currentQuantity == 0 && !productAdded) {
                        line = fields[0] + "|" + fields[1] + "|" +
                                order.getProductId() + "|" + order.getQuantity();
                        productAdded = true;
                    }
                }
                lines.add(line);
            }

            //если не нашли пустую ячейку ... (ну не склад же новый открывать)
            if (!productAdded) {
                System.out.println("Ваш товар... \n отправлен на благотворительность =)");
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String updatedLine : lines) {
                    bw.write(updatedLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                throw new RepositoryException("Ошибка при возврате товара: " + FILE_PATH);
            }
        } catch (FileNotFoundException e) {
                throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка при возврате товара: " + FILE_PATH);
        }
    }
}