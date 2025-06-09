package org.example.service;

import org.example.enumClass.OrderStatus;
import org.example.exception.RepositoryException;
import org.example.model.Order;
import org.example.model.Product;
import org.example.repository.CellRepository;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;

import java.util.HashMap;
import java.util.List;

//все методы для входа как юзера

public class UserService {

    private final OrderRepository orderRepository = new OrderRepository();
    private final CellRepository cellRepository = new CellRepository();
    private final ProductRepository productRepository = new ProductRepository();

    public List<Product> getAllProducts() throws RepositoryException {
        return productRepository.getAllProducts();
    }

    public HashMap<Integer, Integer> getAllProductsWithQuantity() throws RepositoryException {
        return cellRepository.getAllProductQuantity();
    }

    public List<Order> getOppOrders() throws RepositoryException {
        return orderRepository.getOppOrders();
    }

    public List<Order> getIssuedOrders() throws RepositoryException {
        return orderRepository.getIssuedOrders();
    }

    public List<Order> getAcceptedBackOrders() throws RepositoryException {
        return orderRepository.getAcceptedBackOrders();
    }

    public void makeOrder(Order order) throws RepositoryException {
        orderRepository.makeOrder(order);
        cellRepository.updateSells(order.getProductId(), order.getQuantity());
    }

    public void receiveOrder(int orderId) throws RepositoryException {
        orderRepository.updateOrderStatus(orderId, OrderStatus.ISSUED);
    }

    public void returnOrder(int orderId) throws RepositoryException {
        Order order = orderRepository.getAllOrders().stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElseThrow(() -> new RepositoryException("Заказ не найден"));

        orderRepository.updateOrderStatus(orderId, OrderStatus.ACCEPTED_BACK);
        cellRepository.putProduct(order);
    }
}