package org.example.model;

public class Cell {

    private int id;
    private int wareHouseId;
    private int productId;
    private int productQuantity;

    public Cell(int id, int wareHouseId, int productId, int productQuantity) {
        this.id = id;
        this.wareHouseId = wareHouseId;
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public String toString() {
        return  id +
                " " + wareHouseId +
                " " + productId +
                " " + productQuantity;
    }
}
