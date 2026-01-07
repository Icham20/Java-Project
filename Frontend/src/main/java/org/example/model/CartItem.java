package org.example.model;

import java.util.List;

public class CartItem {
    private final Product product;  // Le produit ne change pas
    private int quantity;           // La quantité peut changer
    private final List<String> options; // Les options ne changent pas

    // Constructeur AVEC options
    public CartItem(Product product, int quantity, List<String> options) {
        this.product = product;
        this.quantity = quantity;
        this.options = options;
    }

    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public List<String> getOptions() { return options; }

    // Setter pour la quantité (seule la quantité peut changer)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Calcul du prix total
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    // Méthode utile pour comparer deux items
    public boolean isSameProductWithSameOptions(CartItem other) {
        return this.product.getId().equals(other.product.getId()) &&
                this.options.equals(other.options);
    }
}