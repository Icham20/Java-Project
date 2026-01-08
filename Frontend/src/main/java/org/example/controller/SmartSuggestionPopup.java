package org.example.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Product;
import org.example.services.ApiService;
import org.example.services.CartService;
import org.example.utils.InterfaceTools;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur gérant l'algorithme et l'affichage des suggestions (Pop-up).
 */
public class SmartSuggestionPopup {

    private final ApiService apiService;
    private final CartService cartService;
    private long lastSuggestionTime = 0;
    private boolean suggestionPopupOpen = false;

    public SmartSuggestionPopup(ApiService apiService, CartService cartService) {
        this.apiService = apiService;
        this.cartService = cartService;
    }

    /** Vérifie si le produit ajouté déclenche une suggestion (ex: Plat -> Entrée/Dessert). */
    public void tryShowSuggestion(Product justAddedProduct, Stage ownerStage, ResourceBundle bundle, MainAppController mainController) {
        if (suggestionPopupOpen || System.currentTimeMillis() - lastSuggestionTime < 1000) return;

        Platform.runLater(() -> {
            try {
                List<Product> suggestions = new ArrayList<>();
                // Logique : Si Plat (2) ajouté, proposer Entrée (1), Dessert (3) et Boisson (4)
                if (justAddedProduct.getCategoryId() == 2) {
                    List<Product> starters = apiService.getProductsByCategory(1L);
                    if(!starters.isEmpty()) suggestions.add(starters.get(0));

                    List<Product> desserts = apiService.getProductsByCategory(3L);
                    if(!desserts.isEmpty()) suggestions.add(desserts.get(0));

                    List<Product> drinks = apiService.getProductsByCategory(4L);
                    if(!drinks.isEmpty()) suggestions.add(drinks.get(0));
                }

                if (!suggestions.isEmpty()) {
                    lastSuggestionTime = System.currentTimeMillis();
                    createPopup(suggestions, justAddedProduct, ownerStage, bundle, mainController);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /** Construit la fenêtre modale (Popup) contenant les suggestions. */
    private void createPopup(List<Product> suggestions, Product mainProduct, Stage ownerStage, ResourceBundle bundle, MainAppController mainController) {
        if (suggestionPopupOpen || ownerStage == null) return;
        suggestionPopupOpen = true;

        Stage popup = new Stage();
        popup.initOwner(ownerStage);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.UTILITY);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d97706; -fx-border-width: 3;");

        Label title = new Label(bundle.getString("suggestion.title") + " " + mainController.getTranslateName(mainProduct) + " !");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label subtitle = new Label(bundle.getString("suggestion.subtitle"));
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        HBox suggestionsGrid = new HBox(20);
        suggestionsGrid.setAlignment(Pos.CENTER);
        suggestionsGrid.setPadding(new Insets(20, 0, 20, 0));

        for (Product product : suggestions) {
            suggestionsGrid.getChildren().add(createCard(product, popup, bundle, mainController));
        }

        Button skipBtn = new Button(bundle.getString("suggestion.skip"));
        skipBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 20;");
        skipBtn.setOnAction(e -> {
            suggestionPopupOpen = false;
            popup.close();
        });

        root.getChildren().addAll(title, subtitle, suggestionsGrid, skipBtn);

        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.sizeToScene();

        popup.setOnHidden(e -> suggestionPopupOpen = false);
        popup.setOnCloseRequest(e -> suggestionPopupOpen = false);

        popup.show();
        popup.centerOnScreen();
    }

    /** Crée une carte visuelle pour un produit suggéré. */
    private VBox createCard(Product product, Stage popup, ResourceBundle bundle, MainAppController mainController) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #fbbf24; -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;");

        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #d97706; -fx-border-width: 2; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #fbbf24; -fx-border-width: 1; -fx-border-radius: 10;");
        });

        card.getChildren().add(InterfaceTools.createProductImageNode(product.getImageUrl(), 120, 120));

        Label name = new Label(mainController.getTranslateName(product));
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        name.setWrapText(true);
        name.setMaxWidth(150);
        name.setTextAlignment(TextAlignment.CENTER);

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d97706;");

        Button addBtn = new Button(bundle.getString("suggestion.add"));
        addBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 15; -fx-cursor: hand;");

        addBtn.setOnAction(e -> {
            List<String> options = new ArrayList<>();
            if (product.getCategoryId() == 4) options.add(bundle.getString("option.with_ice"));
            else if (product.getCategoryId() == 3) options.add("Standard");

            cartService.addProduct(product, 1, options);
            suggestionPopupOpen = false;
            popup.close();
            InterfaceTools.showQuickNotification("✅ " + mainController.getTranslateName(product) + " " + bundle.getString("notification.added"));
        });

        card.getChildren().addAll(name, price, addBtn);
        return card;
    }
}