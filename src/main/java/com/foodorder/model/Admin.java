package com.foodorder.model;

public class Admin extends User {

    public Admin() {
        super(0, "", "", "");
    }

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
