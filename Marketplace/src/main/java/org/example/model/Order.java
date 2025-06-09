package org.example.model;

import org.example.enumClass.OrderStatus;

import java.time.LocalDate;

public class Order {

    private int id;
    private int productId;
    private int quantity;
    private OrderStatus orderStatus;
    private LocalDate date;
    private int oppID;

    public Order(int id, int productId, int quantity, OrderStatus orderStatus, int oppID) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
        setDate(LocalDate.now());
        this.oppID = oppID;
    }

    public Order(int id, int productId, int quantity, OrderStatus orderStatus, LocalDate date, int oppID) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
        this.date = date;
        this.oppID = oppID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getOppId() {
        return oppID;
    }

    public void setOppId(int oppID) {
        this.oppID = oppID;
    }

    @Override
    public String toString() {
        return  id +
                " " + orderStatus +
                " " + date +
                " " + oppID;
    }
}