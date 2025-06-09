package org.example.model;

public class Warehouse {

    private int id;
    private String address;
    private int cellsQuantity;

    public Warehouse(int id, String address, int cellsQuantity) {
        this.id = id;
        this.address = address;
        this.cellsQuantity = cellsQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public int getCellsQuantity() {
        return cellsQuantity;
    }

    public void setCellsQuantity(int cellsQuantity) {
        this.cellsQuantity = cellsQuantity;
    }

    @Override
    public String toString() {
        return  "id-" + id +
                " " + address +
                "\n  Кол-во ячеек: " + cellsQuantity;
    }
}