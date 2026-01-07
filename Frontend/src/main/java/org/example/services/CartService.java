package org.example.services;

import org.example.model.CartItem;
import org.example.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private static CartService instance;
    private final List<CartItem> items;

    private CartService() {
        items = new ArrayList<>();
    }

    public static CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    // Ajouter un produit au panier
    public void addProduct(Product product, int quantity, List<String> options) {
        // Vérifie si le produit avec les mêmes options existe déjà
        CartItem newItem = new CartItem(product, quantity, options);

        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId()) &&
                    item.getOptions().equals(options)) {
                // Même produit avec mêmes options : augmente la quantité
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Sinon, ajoute un nouvel item
        items.add(newItem);
    }

    // Supprimer un item par index
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    // Vider le panier
    public void clear() {
        items.clear();
    }

    // Calculer le total
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // Nombre d'articles (items différents)
    public int getItemCount() {
        return items.size();
    }

    // Nombre total de produits (quantités cumulées)
    public int getTotalProductCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    // Getters
    public List<CartItem> getItems() {
        return new ArrayList<>(items); // Retourne une copie
    }

    // Méthode pour mettre à jour la quantité d'un item
    public void updateQuantity(int index, int newQuantity) {
        if (index >= 0 && index < items.size() && newQuantity > 0) {
            items.get(index).setQuantity(newQuantity);
        }
    }
}