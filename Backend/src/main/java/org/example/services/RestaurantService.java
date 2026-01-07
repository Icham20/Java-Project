package org.example.services;

import org.example.model.*; // IMPORTANT: model, pas models !
import org.example.repository.*;
import java.util.List;

public class RestaurantService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public RestaurantService() {
        this.productRepository = new ProductRepository();
        this.categoryRepository = new CategoryRepository();
        this.orderRepository = new OrderRepository();
    }

    // === CATÉGORIES ===
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(); // Utilise findAll()
    }

    public Category getCategoryById(int id) {
        // Option 1: Filtrer depuis la liste
        return getAllCategories().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);

        // Option 2: Demander à ton collègue d'ajouter findById() dans CategoryRepository
    }

    // === PRODUITS ===
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Utilise findAll()
    }

    public Product getProductById(int id) {
        return productRepository.findById(id); // Utilise findById()
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return productRepository.findByCategory(categoryId); // Utilise findByCategory()
    }

    // === COMMANDES ===
    public int createOrder(Order order) {
        // 1. Vérifier et décrémenter le stock pour chaque article
        for (OrderItem item : order.getItems()) {
            boolean success = productRepository.updateStock(item.getProductId(), item.getQuantity());
            if (!success) {
                // En réalité, il faudrait faire un rollback transactionnel ici
                throw new RuntimeException("Stock insuffisant pour le produit ID: " + item.getProductId());
            }
        }
        
        // 2. Si tout est bon, on sauvegarde la commande
        return orderRepository.save(order);
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id); // Utilise findById()
    }
}