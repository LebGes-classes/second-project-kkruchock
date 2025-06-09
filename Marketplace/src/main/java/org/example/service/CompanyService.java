package org.example.service;

import org.example.exception.RepositoryException;
import org.example.model.*;
import org.example.repository.*;

import java.util.List;

//все методы для управления от компании

public class CompanyService {

    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private final OrderPickUpPointRepository oppRepository = new OrderPickUpPointRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final CellRepository cellRepository = new CellRepository();
    private final OrderRepository orderRepository = new OrderRepository();

    public void openWarehouse(Warehouse warehouse) throws RepositoryException {
        warehouseRepository.openWarehouse(warehouse);
        cellRepository.createNewEmptyCells(warehouse.getId(), warehouse.getCellsQuantity());
    }

    public void closeWarehouse(int warehouseId) throws RepositoryException {
        warehouseRepository.closeWarehouse(warehouseId);
        cellRepository.deleteCellsByWarehouse(warehouseId);
    }

    public void openOrderPickUpPoint(OrderPickUpPoint opp) throws RepositoryException {
        oppRepository.openOrderPickUpPoint(opp);
    }

    public void closeOrderPickUpPoint(int oppId) throws RepositoryException {
        oppRepository.closeOrderPickUpPoint(oppId);
        orderRepository.updateOrdersForCloseOpp(oppId);
    }

    public void hireEmployee(Employee employee) throws RepositoryException {
        employeeRepository.takeEmployee(employee);
    }

    public void fireEmployee(int employeeId) throws RepositoryException {
        employeeRepository.takeoutEmployee(employeeId);
    }

    public void addProduct(Product product) throws RepositoryException {
        productRepository.addNewProduct(product);
    }

    public int getEmptyCellsCount(int warehouseId) throws RepositoryException {
        return cellRepository.countEmptyCells(warehouseId);
    }

    public List<Warehouse> getAllWarehouse() throws RepositoryException {
        return warehouseRepository.getAllWarehouse();
    }

    public List<OrderPickUpPoint> getAllOops() throws RepositoryException {
        return oppRepository.getAllOrderPickUpPoints();
    }

    public List<Employee> getAllEmployee() throws RepositoryException {
        return employeeRepository.getAllEmployees();
    }
}