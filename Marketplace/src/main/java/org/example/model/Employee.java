package org.example.model;

public class Employee {

    private int id;
    private String name;
    private int workPlaceId;
    private String workPlaceName;

    public Employee(int id, String name, int workPlaceId, String workPlaceName) {
        this.id = id;
        this.name = name;
        this.workPlaceId = workPlaceId;
        this.workPlaceName = workPlaceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkPlaceId() {
        return workPlaceId;
    }

    public void setWorkPlaceId(int workPlaceId) {
        this.workPlaceId = workPlaceId;
    }

    public String getWorkPlaceName() {
        return workPlaceName;
    }

    public void setWorkPlaceName(String workPlaceName) {
        this.workPlaceName = workPlaceName;
    }

    @Override
    public String toString() {
        return  id +
                " " + name +
                " " + workPlaceId +
                " " + workPlaceName;
    }
}