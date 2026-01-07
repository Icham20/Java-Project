package org.example;

import io.javalin.Javalin;
import org.example.controllers.RestaurantController;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 API Restaurant avec MySQL démarrée...");

        RestaurantController controller = new RestaurantController();

        Javalin app = Javalin.create().start(7000);

        // ============= TES ROUTES =============

        // Accueil
        app.get("/", ctx -> ctx.result("API Restaurant avec MySQL"));

        // Catégories
        app.get("/api/categories", controller::getCategories);

        // Produits
        app.get("/api/products", controller::getProducts);
        app.get("/api/products/{id}", controller::getProductById);
        app.get("/api/categories/{categoryId}/products", controller::getProductsByCategory);

        // Commandes
        app.post("/api/orders", controller::createOrder);
        app.get("/api/orders/{id}", controller::getOrderById);

        System.out.println("✅ API prête sur http://localhost:7000");
        System.out.println("🗄️  Connexion à MySQL activée");
    }
}