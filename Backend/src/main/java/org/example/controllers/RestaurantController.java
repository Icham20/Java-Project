package org.example.controllers;

import io.javalin.http.Context;
import org.example.services.RestaurantService;
import org.example.model.*; // model, pas models !
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.example.DTO.ProductDTO;
import org.example.DTO.CategoryDTO;
import java.util.stream.Collectors;

public class RestaurantController {
    private final RestaurantService service;
    private final ObjectMapper objectMapper;

    public RestaurantController() {
        this.service = new RestaurantService();
        this.objectMapper = new ObjectMapper();
    }

    // === CATÉGORIES ===
    public void getCategories(Context ctx) {
        try {
            // 1. Récupère les catégories de la BDD
            List<Category> categories = service.getAllCategories();

            // 2. Convertit chaque Category en CategoryDTO
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(category -> new CategoryDTO(category))  // Conversion
                    .collect(Collectors.toList());               // Rassemble en liste

            // 3. Envoie les DTO (pas les entités BDD)
            ctx.json(categoryDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    // === PRODUITS ===
    public void getProducts(Context ctx) {
        try {
            List<Product> products;

            // Vérifie si le frontend demande un filtre
            String categoryParam = ctx.queryParam("category");

            if (categoryParam != null && !categoryParam.isEmpty()) {
                // Appel: /api/products?category=1
                int categoryId = Integer.parseInt(categoryParam);
                products = service.getProductsByCategory(categoryId);
            } else {
                // Appel: /api/products
                products = service.getAllProducts();
            }

            // Conversion Product → ProductDTO
            List<ProductDTO> productDTOs = products.stream()
                    .map(product -> new ProductDTO(product))
                    .collect(Collectors.toList());

            ctx.json(productDTOs);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "ID de catégorie invalide"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    public void getProductById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Product product = service.getProductById(id);

            if (product != null) {
                // Conversion unique Product → ProductDTO
                ProductDTO productDTO = new ProductDTO(product);
                ctx.json(productDTO);
            } else {
                ctx.status(404).json(Map.of(
                        "error", "Produit non trouvé",
                        "id", id
                ));
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "ID invalide"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    // Route pour produits par catégorie
    public void getProductsByCategory(Context ctx) {
        try {
            int categoryId = Integer.parseInt(ctx.pathParam("categoryId"));
            List<Product> products = service.getProductsByCategory(categoryId);

            // Conversion
            List<ProductDTO> productDTOs = products.stream()
                    .map(product -> new ProductDTO(product))
                    .collect(Collectors.toList());

            ctx.json(productDTOs);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "ID de catégorie invalide"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    // === COMMANDES ===
    public void createOrder(Context ctx) {
        try {
            // 1. Lire le JSON
            String body = ctx.body();
            System.out.println("📦 Commande reçue: " + body);

            // 2. Convertir en objet Order
            Order order = objectMapper.readValue(body, Order.class);

            // 3. Valider
            if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "Nom client requis"));
                return;
            }

            if (order.getItems() == null || order.getItems().isEmpty()) {
                ctx.status(400).json(Map.of("error", "La commande doit contenir au moins un article"));
                return;
            }

            // 4. Sauvegarder
            int orderId = service.createOrder(order);

            if (orderId > 0) {
                // 5. Réponse avec succès
                Map<String, Object> response = Map.of(
                        "success", true,
                        "orderId", orderId,
                        "orderNumber", "CMD-" + orderId,
                        "message", "Commande créée avec succès"
                );
                ctx.status(201).json(response);
                System.out.println("✅ Commande créée: CMD-" + orderId);
            } else {
                ctx.status(500).json(Map.of("error", "Échec de la création de la commande"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).json(Map.of(
                    "error", "Erreur de traitement",
                    "message", e.getMessage()
            ));
        }
    }

    public void getOrderById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Order order = service.getOrderById(id);

            if (order != null) {
                ctx.json(order);
            } else {
                ctx.status(404).json(Map.of(
                        "error", "Commande non trouvée",
                        "id", id
                ));
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "ID invalide"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}