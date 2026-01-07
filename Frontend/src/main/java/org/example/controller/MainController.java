package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import org.example.controller.views.*;
import org.example.model.Product;
import org.example.service.ApiService;
import org.example.service.CartService;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {

    @FXML private BorderPane mainLayout;

    // Services accessibles par les autres vues via des getters
    private final ApiService apiService = new ApiService();
    private final CartService cartService = CartService.getInstance();
    private ResourceBundle bundle;
    private Locale currentLocale = new Locale("fr");

    @FXML
    public void initialize() {
        if(mainLayout != null) {
            mainLayout.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());
            loadLanguage("fr");
        }
    }

    public void loadLanguage(String lang) {
        currentLocale = new Locale(lang);
        this.bundle = ResourceBundle.getBundle("org.example.strings", currentLocale);
        // Au changement de langue, on recharge l'accueil
        showHomeScreen();
    }

    // --- NAVIGATION ---

    // Méthode utilitaire pour changer le centre de l'écran
    public void setView(Node node) {
        mainLayout.setCenter(node);
    }

    public void showHomeScreen() {
        // On délègue la création de la vue à la classe HomeView
        setView(new HomeView(this).getView());
    }

    public void showMenuScreen() {
        setView(new MenuView(this).getView());
    }

    public void showDetailScreen(Product p) {
        setView(new DetailView(this, p).getView());
    }

    public void showCartScreen() {
        setView(new CartView(this).getView());
    }

    public void showConfirmationScreen(int orderId) {
        setView(new ConfirmationView(this, orderId).getView());
    }

    // --- GETTERS (Pour que les sous-vues accèdent aux données) ---
    public ApiService getApiService() { return apiService; }
    public CartService getCartService() { return cartService; }
    public ResourceBundle getBundle() { return bundle; }
}