package org.example.model;

public class OrderPickUpPoint {

    private int id;
    private String address;

    public OrderPickUpPoint(int id, String address) {
        this.id = id;
        this.address = address;
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

    @Override
    public String toString() {
        return  "id-" + id +
                " " + address;
    }
}