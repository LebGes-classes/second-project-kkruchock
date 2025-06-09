package org.example.repository;

import org.example.exception.RepositoryException;
import org.example.model.Employee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EmployeeRepository {

    private static final String FILE_PATH = "src/main/resources/Employee";
    private static final String HEADER = "id|name|work_place_id|work_place_name";

    //чтение файла
    public List<Employee> getAllEmployees() throws RepositoryException {

        List<Employee> employees = new ArrayList<>();

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
                    Employee employee = new Employee(
                            Integer.parseInt(fields[0].trim()),
                            fields[1].trim(),
                            Integer.parseInt(fields[2].trim()),
                            fields[3].trim()
                    );
                    employees.add(employee);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RepositoryException("Файл не найден " + FILE_PATH);
        } catch (IOException e) {
            throw new RepositoryException("Ошибка чтения файла " + FILE_PATH);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Ошибка формата данных в файле: " + FILE_PATH);
        }

        return  employees;
    }

    //нанять сотрудника
    public void takeEmployee(Employee employee) throws RepositoryException {

        List<Employee> existingEmployees = getAllEmployees();

        for (Employee existing : existingEmployees) {
            if (existing.getId() == employee.getId()) {
                throw new RepositoryException("Склад с ID " + employee.getId() + " уже существует");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

        String line = String.join("|",
                String.valueOf(employee.getId()),
                employee.getName(),
                String.valueOf(employee.getWorkPlaceId()),
                String.valueOf(employee.getWorkPlaceName())
        );

        bw.write(line);
        bw.newLine();

    } catch (FileNotFoundException e) {
        throw new RepositoryException("Файл со складами не найден: " + FILE_PATH);
    } catch (IOException e) {
        throw new RepositoryException("Ошибка записи в файл со складами: " + FILE_PATH);
    }
}

    //увольнение
    public void takeoutEmployee(int id) throws RepositoryException {

        List<Employee> employees = getAllEmployees();
        boolean isFound = false;

        //ищем нужного работника и увольняем
        Iterator<Employee> iterator = employees.iterator();
        while (iterator.hasNext()) {
            Employee employee = iterator.next();
            if (employee.getId() == id) {
                iterator.remove();
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            throw new RepositoryException("Работник с ID " + id + " не найден");
        }

        //перезаписываем файл
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            bw.write(HEADER);
            bw.newLine();

            for (Employee employee : employees) {
                String line = String.join("|",
                        String.valueOf(employee.getId()),
                        employee.getName(),
                        String.valueOf(employee.getWorkPlaceId()),
                        String.valueOf(employee.getWorkPlaceName())
                );

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