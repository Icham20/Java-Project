package org.example.services;

import org.example.model.CartItem;
import org.example.model.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartService {
    private static CartService instance;
    private final List<CartItem> items;
    private final ApiService apiService;

    private CartService() {
        items = new ArrayList<>();
        apiService = new ApiService();
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

    // NOUVELLE MÉTHODE : Récupérer toutes les suggestions pour un plat donné
    public List<Product> getLimitedSuggestions(Product mainProduct) {
        List<Product> allProducts = apiService.getAllProducts();
        List<Product> suggestions = new ArrayList<>();

        Set<Long> productIdsInCart = items.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());

        List<Product> availableProducts = allProducts.stream()
                .filter(p -> !p.getId().equals(mainProduct.getId()))
                .filter(p -> !productIdsInCart.contains(p.getId()))
                .filter(Product::isAvailable)
                .collect(Collectors.toList());

        // Créer une "graine" basée sur le plat pour avoir des suggestions constantes
        String seedString = mainProduct.getName() + mainProduct.getId();
        int seed = Math.abs(seedString.hashCode());

        // Fonction pour sélectionner aléatoirement mais de manière constante
        java.util.Random random = new java.util.Random(seed);

        if (mainProduct.getCategoryId() == 2) { // PLAT
            // Desserts
            List<Product> desserts = availableProducts.stream()
                    .filter(p -> p.getCategoryId() == 3)
                    .collect(Collectors.toList());
            if (!desserts.isEmpty()) {
                suggestions.add(desserts.get(random.nextInt(desserts.size())));
            }

            // Boissons
            List<Product> drinks = availableProducts.stream()
                    .filter(p -> p.getCategoryId() == 4)
                    .collect(Collectors.toList());
            if (!drinks.isEmpty()) {
                suggestions.add(drinks.get(random.nextInt(drinks.size())));
            }

            // Entrées
            List<Product> entrees = availableProducts.stream()
                    .filter(p -> p.getCategoryId() == 1)
                    .collect(Collectors.toList());
            if (!entrees.isEmpty()) {
                suggestions.add(entrees.get(random.nextInt(entrees.size())));
            }
        }
        // ... autres catégories similaires

        return suggestions.stream()
                .distinct()
                .limit(3)
                .collect(Collectors.toList());
    }


}


