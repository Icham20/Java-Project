package org.example.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private String options;

    public OrderItem() {}

    public OrderItem(int productId, int quantity, double unitPrice, String options) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.options = options;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
}
