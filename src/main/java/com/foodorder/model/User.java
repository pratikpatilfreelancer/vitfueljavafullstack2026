package com.foodorder.model;

public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /** Demonstrates polymorphism: each subclass reports its own role. */
    public abstract String getRole();

    @Override
    public String toString() {
        return name + " (" + email + ") [" + getRole() + "]";
    }
}
