package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private double totalPrice;
    private String createdAt; // String pour simplifier le JSON
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(int id, String customerName, double totalPrice, String createdAt, List<OrderItem> items) {
        this.id = id;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.items = items;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}