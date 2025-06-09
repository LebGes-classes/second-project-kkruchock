package org.example.repository;

import org.example.exception.RepositoryException;
import org.example.model.Product;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private static final String FILE_PATH = "src/main/resources/Product";
    private static final String HEADER = "id|name|price";

    //чтение файла
    public List<Product> getAllProducts() throws RepositoryException {

        List<Product> productList = new ArrayList<>();

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
                if (fields.length == 3) {
                    Product product = new Product(
                            Integer.parseInt(fields[0].trim()),
                            fields[1].trim(),
                            Double.parseDouble(fields[2].trim())
                    );
                    productList.add(product);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла " + FILE_PATH);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Ошибка формата данных в файле: " + FILE_PATH);
        }

        return  productList;
    }

    //добавляем новый товар
    public void addNewProduct(Product newProduct) throws RepositoryException {

        List<Product> products = getAllProducts();
        for (Product product : products) {
            if (product.getId() == newProduct.getId()) {
                throw new RepositoryException("Товар с ID " + product.getId() + " уже сузествует");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String line = String.join("|",
                    String.valueOf(newProduct.getId()),
                    newProduct.getName(),
                    String.valueOf(newProduct.getPrice()));

            bw.write(line);
            bw.newLine();
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл с товарами не найден: " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка записи в файл с товарами: " + FILE_PATH);
        }
    }
}