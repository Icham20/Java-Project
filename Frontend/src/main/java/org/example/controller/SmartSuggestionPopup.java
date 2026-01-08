

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
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Contrôleur gérant l'algorithme et l'affichage des suggestions (Pop-up).
 */
public class SmartSuggestionPopup {

    private final ApiService apiService;
    private final CartService cartService;
    private long lastSuggestionTime = 0;
    private boolean suggestionPopupOpen = false;
    private final Random random = new Random();


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
                    if(!starters.isEmpty()) {
                        // MODIFICATION ICI
                        Product randomStarter = starters.get(random.nextInt(starters.size()));
                        suggestions.add(randomStarter);
                    }

                    List<Product> desserts = apiService.getProductsByCategory(3L);
                    if(!desserts.isEmpty()) {
                        // MODIFICATION ICI
                        Product randomDessert = desserts.get(random.nextInt(desserts.size()));
                        suggestions.add(randomDessert);
                    }

                    List<Product> drinks = apiService.getProductsByCategory(4L);
                    if(!drinks.isEmpty()) {
                        // MODIFICATION ICI
                        Product randomDrink = drinks.get(random.nextInt(drinks.size()));
                        suggestions.add(randomDrink);
                    }
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
        popup.initStyle(StageStyle.UNDECORATED);

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

        // MODIFICATION PRINCIPALE ICI :
        addBtn.setOnAction(e -> {
            if (product.getCategoryId() == 4) { // BOISSON
                showSimpleDrinkOptions(product, popup, bundle, mainController);
            } else if (product.getCategoryId() == 3) { // DESSERT
                showSimpleDessertOptions(product, popup, bundle, mainController);
            } else {
                // Pour les autres catégories (entrées, plats), ajouter directement
                cartService.addProduct(product, 1, new ArrayList<>());
                suggestionPopupOpen = false;
                popup.close();
                InterfaceTools.showQuickNotification("✅ " + mainController.getTranslateName(product) + " " + bundle.getString("notification.added"));
            }
        });

        card.getChildren().addAll(name, price, addBtn);
        return card;
    }

    private void showSimpleDrinkOptions(Product drink, Stage parentPopup, ResourceBundle bundle, MainAppController mainController) {
        // Créer un petit popup d'options
        Stage optionsStage = new Stage();
        optionsStage.initOwner(parentPopup);
        optionsStage.initModality(Modality.WINDOW_MODAL);
        optionsStage.initStyle(StageStyle.UNDECORATED);
        optionsStage.setTitle(bundle.getString("drink.options.title"));

        VBox optionsRoot = new VBox(15);
        optionsRoot.setPadding(new Insets(20));
        optionsRoot.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Titre - Utiliser la clé qui existe
        String drinkName = mainController.getTranslateName(drink);
        Label title = new Label(bundle.getString("drink.options.for") + " " + drinkName);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Options selon le type de boisson
        VBox optionsBox = new VBox(10);

        if (drinkName.toLowerCase().contains("café") ||
                drinkName.toLowerCase().contains("cafe") ||
                drinkName.toLowerCase().contains("coffee")) {
            // Options pour le café
            Label coffeeLabel = new Label(bundle.getString("drink.options.coffee"));
            coffeeLabel.setStyle("-fx-font-weight: bold;");

            HBox coffeeOptions = new HBox(10);
            coffeeOptions.setAlignment(Pos.CENTER_LEFT);

            Button withCoffee = new Button(bundle.getString("drink.options.with_coffee"));
            withCoffee.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-font-weight: bold; -fx-padding: 8 15;");

            Button withoutCoffee = new Button(bundle.getString("drink.options.without_coffee"));
            withoutCoffee.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 8 15;");

            withCoffee.setOnAction(ev -> {
                List<String> options = new ArrayList<>();
                options.add(bundle.getString("drink.options.with_coffee"));
                cartService.addProduct(drink, 1, options);
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + drinkName + " " + bundle.getString("notification.added"));
            });

            withoutCoffee.setOnAction(ev -> {
                cartService.addProduct(drink, 1, new ArrayList<>());
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + drinkName + " " + bundle.getString("notification.added"));
            });

            coffeeOptions.getChildren().addAll(withCoffee, withoutCoffee);
            optionsBox.getChildren().addAll(coffeeLabel, coffeeOptions);

        } else {
            // Options pour les autres boissons (glaçons)
            Label iceLabel = new Label(bundle.getString("drink.options.ice"));
            iceLabel.setStyle("-fx-font-weight: bold;");

            HBox iceOptions = new HBox(10);
            iceOptions.setAlignment(Pos.CENTER_LEFT);

            Button withIce = new Button(bundle.getString("drink.options.with_ice"));
            withIce.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af; -fx-font-weight: bold; -fx-padding: 8 15;");

            Button withoutIce = new Button(bundle.getString("drink.options.without_ice"));
            withoutIce.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 8 15;");

            withIce.setOnAction(ev -> {
                List<String> options = new ArrayList<>();
                options.add(bundle.getString("drink.options.with_ice"));
                cartService.addProduct(drink, 1, options);
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + drinkName + " " + bundle.getString("notification.added"));
            });

            withoutIce.setOnAction(ev -> {
                List<String> options = new ArrayList<>();
                options.add(bundle.getString("drink.options.without_ice"));
                cartService.addProduct(drink, 1, options);
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + drinkName + " " + bundle.getString("notification.added"));
            });

            iceOptions.getChildren().addAll(withIce, withoutIce);
            optionsBox.getChildren().addAll(iceLabel, iceOptions);
        }

        // Bouton Annuler - Utiliser la clé qui existe
        Button cancelBtn = new Button(bundle.getString("drink.options.cancel"));
        cancelBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 8 20;");
        cancelBtn.setOnAction(ev -> optionsStage.close());

        optionsRoot.getChildren().addAll(title, optionsBox, cancelBtn);

        Scene scene = new Scene(optionsRoot);
        optionsStage.setScene(scene);
        optionsStage.setResizable(false);
        optionsStage.sizeToScene();
        optionsStage.centerOnScreen();
        optionsStage.show();
    }

    private void showSimpleDessertOptions(Product dessert, Stage parentPopup, ResourceBundle bundle, MainAppController mainController) {
        String dessertName = mainController.getTranslateName(dessert);

        // Créer une petite popup d'options
        Stage optionsStage = new Stage();
        optionsStage.initOwner(parentPopup);
        optionsStage.initModality(Modality.WINDOW_MODAL);
        optionsStage.initStyle(StageStyle.UNDECORATED);

        // UTILISER LA CLÉ DE TRADUCTION
        optionsStage.setTitle(bundle.getString("dessert.options.title"));

        VBox optionsRoot = new VBox(15);
        optionsRoot.setPadding(new Insets(20));
        optionsRoot.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Titre - UTILISER LA CLÉ DE TRADUCTION
        Label title = new Label(bundle.getString("dessert.options.for") + " " + dessertName);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Options pour les desserts
        VBox optionsBox = new VBox(10);

        // Option café (pour certains desserts)
        if (dessertName.toLowerCase().contains("nougat") ||
                dessertName.toLowerCase().contains("perles") ||
                dessertName.toLowerCase().contains("coconut") ||
                dessertName.toLowerCase().contains("sesame")) {
            // Desserts qui vont bien avec le café
            Label coffeeLabel = new Label(bundle.getString("drink.options.coffee")); // Utiliser la même clé que pour les boissons
            coffeeLabel.setStyle("-fx-font-weight: bold;");

            HBox coffeeOptions = new HBox(10);
            coffeeOptions.setAlignment(Pos.CENTER_LEFT);

            // UTILISER LES CLÉS DE TRADUCTION
            Button withCoffee = new Button(bundle.getString("dessert.options.with_coffee"));
            withCoffee.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-font-weight: bold; -fx-padding: 8 15;");

            Button withoutCoffee = new Button(bundle.getString("dessert.options.without"));
            withoutCoffee.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 8 15;");

            withCoffee.setOnAction(ev -> {
                List<String> options = new ArrayList<>();
                options.add(bundle.getString("dessert.options.with_coffee"));
                cartService.addProduct(dessert, 1, options);
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + dessertName + " " + bundle.getString("notification.added"));
            });

            withoutCoffee.setOnAction(ev -> {
                cartService.addProduct(dessert, 1, new ArrayList<>());
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + dessertName + " " + bundle.getString("notification.added"));
            });

            coffeeOptions.getChildren().addAll(withCoffee, withoutCoffee);
            optionsBox.getChildren().addAll(coffeeLabel, coffeeOptions);
        } else {
            // Pour les fruits frais (mangue, papaye) - option simple
            // ICI AUSSI, VOUS POURRIEZ AJOUTER UNE CLÉ DE TRADUCTION SI VOUS VOULEZ
            Label simpleLabel = new Label("Votre dessert sera servi frais");
            simpleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #16a34a;");

            HBox simpleOptions = new HBox(10);
            simpleOptions.setAlignment(Pos.CENTER);

            // Utiliser une clé de traduction
            Button confirmBtn = new Button("🍨 " + bundle.getString("dessert.options.without"));
            confirmBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");

            confirmBtn.setOnAction(ev -> {
                cartService.addProduct(dessert, 1, new ArrayList<>());
                optionsStage.close();
                parentPopup.close();
                suggestionPopupOpen = false;
                InterfaceTools.showQuickNotification("✅ " + dessertName + " " + bundle.getString("notification.added"));
            });

            simpleOptions.getChildren().add(confirmBtn);
            optionsBox.getChildren().addAll(simpleLabel, simpleOptions);
        }

        // Bouton Annuler - UTILISER LA CLÉ DE TRADUCTION
        Button cancelBtn = new Button(bundle.getString("dessert.options.cancel"));
        cancelBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 8 20;");
        cancelBtn.setOnAction(ev -> optionsStage.close());

        optionsRoot.getChildren().addAll(title, optionsBox, cancelBtn);

        Scene scene = new Scene(optionsRoot);
        optionsStage.setScene(scene);
        optionsStage.setResizable(false);
        optionsStage.sizeToScene();
        optionsStage.centerOnScreen();
        optionsStage.show();
    }
}