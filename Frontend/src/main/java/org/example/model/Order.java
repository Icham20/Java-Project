package org.example.model;

import java.util.List;

public class Order {
    private String customerName;
    private double totalPrice;
    private List<OrderItem> items;

    public Order(String customerName, double totalPrice, List<OrderItem> items) {
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}