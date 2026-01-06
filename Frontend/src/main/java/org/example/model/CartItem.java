package org.example.model;
import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private Product product;
    private int quantity;
    private List<String> options;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.options = new ArrayList<>();
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public List<String> getOptions() { return options; }
    public double getTotalPrice() { return product.getPrice() * quantity; }
}