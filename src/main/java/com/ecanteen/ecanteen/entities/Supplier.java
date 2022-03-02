package com.ecanteen.ecanteen.entities;

public class Supplier {
    private String id;
    private String name;
    private String lastSuppliedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastSuppliedDate() {
        return lastSuppliedDate;
    }

    public void setLastSuppliedDate(String lastSuppliedDate) {
        this.lastSuppliedDate = lastSuppliedDate;
    }

    @Override
    public String toString() {
        return name;
    }
}
