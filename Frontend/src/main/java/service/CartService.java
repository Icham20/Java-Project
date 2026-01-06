package org.example.service;
import org.example.model.CartItem;
import org.example.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private static CartService instance;
    private List<CartItem> items;

    private CartService() { this.items = new ArrayList<>(); }

    public static CartService getInstance() {
        if (instance == null) instance = new CartService();
        return instance;
    }

    public void addProduct(Product product, int quantity, List<String> options) {
        CartItem item = new CartItem(product, quantity);
        item.getOptions().addAll(options);
        items.add(item);
    }

    public List<CartItem> getItems() { return items; }
    public void clear() { items.clear(); }
    public void removeProduct(CartItem item) { items.remove(item); }
    public double getTotal() { return items.stream().mapToDouble(CartItem::getTotalPrice).sum(); }
}