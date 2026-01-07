package org.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.model.CartItem;
import org.example.services.CartService;
import org.example.model.Category;
import org.example.model.Product;
import org.example.services.ApiService;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {

    @FXML private BorderPane mainLayout;

    private final ApiService apiService = new ApiService();
    private final CartService cartService = CartService.getInstance();

    private Category currentCategory;
    private ResourceBundle bundle;
    private Locale currentLocale = new Locale("fr");

    @FXML
    public void initialize() {
        if(mainLayout != null) {
            mainLayout.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());
            loadLanguage("fr");
        }
    }

    private void loadLanguage(String lang) {
        currentLocale = new Locale(lang);
        this.bundle = ResourceBundle.getBundle("org.example.strings", currentLocale);
        showHomeScreen();
    }

    // --- 1. ÉCRAN D'ACCUEIL ---
    private void showHomeScreen() {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);

        Label logo = new Label(bundle.getString("app.title"));
        logo.getStyleClass().add("title-large");

        Button startBtn = new Button(bundle.getString("home.cta"));
        startBtn.getStyleClass().add("btn-start");
        startBtn.setOnAction(e -> showMenuScreen());

        Label subTitle = new Label(bundle.getString("home.subtitle"));
        subTitle.getStyleClass().add("subtitle");

        HBox langBox = new HBox(20);
        langBox.setAlignment(Pos.CENTER);

        Button btnFR = createLangBtn("FR");
        btnFR.setOnAction(e -> loadLanguage("fr"));

        Button btnEN = createLangBtn("EN");
        btnEN.setOnAction(e -> loadLanguage("en"));

        Button btnHelp = createLangBtn(bundle.getString("home.help"));

        langBox.getChildren().addAll(btnFR, btnEN, btnHelp);
        root.getChildren().addAll(logo, subTitle, startBtn);

        BorderPane pane = new BorderPane();
        pane.setCenter(root);
        pane.setBottom(langBox);
        BorderPane.setMargin(langBox, new Insets(0,0,40,0));

        mainLayout.setCenter(pane);
    }

    private Button createLangBtn(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-lang");
        b.setPrefWidth(80);
        b.setPrefHeight(40);
        return b;
    }

    // --- 2. ÉCRAN DU MENU ---
    private void showMenuScreen() {
        BorderPane menuLayout = new BorderPane();
        menuLayout.setPadding(new Insets(20, 40, 20, 40));

        // --- EN-TÊTE ---
        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label lblMenu = new Label(bundle.getString("menu.title"));
        lblMenu.getStyleClass().add("menu-title-orange");

        HBox tabs = new HBox(10);
        tabs.setAlignment(Pos.CENTER);
        HBox.setHgrow(tabs, Priority.ALWAYS);

        // Récupérer les catégories depuis l'API
        List<Category> categories = apiService.getCategories();

        if (categories.isEmpty()) {
            showErrorScreen("Impossible de récupérer les catégories depuis le serveur.");
            return;
        }

        // Sélectionner la première catégorie par défaut
        if (currentCategory == null) {
            currentCategory = categories.get(0);
        }

        // Créer les onglets des catégories
        for (Category cat : categories) {
            Button tab = new Button(cat.getName());
            tab.getStyleClass().add("tab-button");

            if (currentCategory.getId().equals(cat.getId())) {
                tab.getStyleClass().add("tab-active");
            }

            tab.setOnAction(e -> {
                currentCategory = cat;
                showMenuScreen();
            });
            tabs.getChildren().add(tab);
        }

        // Bouton panier dans l'en-tête
        Button btnCartTop = new Button("🛒 " + String.format("%.2f €", cartService.getTotal()));
        btnCartTop.getStyleClass().add("btn-primary");
        btnCartTop.setOnAction(e -> showCartScreen());

        header.getChildren().addAll(lblMenu, tabs, btnCartTop);
        menuLayout.setTop(header);

        // --- CONTENU CENTRAL : PRODUITS ---
        List<Product> products = apiService.getProductsByCategory(currentCategory.getId());

        if (products.isEmpty()) {
            VBox noProductsBox = new VBox(20);
            noProductsBox.setAlignment(Pos.CENTER);
            noProductsBox.setPadding(new Insets(50));

            Label noProductsLabel = new Label("Aucun produit disponible dans cette catégorie.");
            noProductsLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 18px;");

            noProductsBox.getChildren().add(noProductsLabel);
            menuLayout.setCenter(noProductsBox);
        } else {
            TilePane grid = new TilePane();
            grid.setHgap(30);
            grid.setVgap(30);
            grid.setPrefColumns(2);
            grid.setAlignment(Pos.TOP_CENTER);
            grid.setPadding(new Insets(20));

            for (Product product : products) {
                grid.getChildren().add(createProductCard(product));
            }

            ScrollPane scroll = new ScrollPane(grid);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            menuLayout.setCenter(scroll);
        }

        // --- PIED DE PAGE ---
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(20, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);

        Button btnAccueil = new Button(bundle.getString("menu.back"));
        btnAccueil.getStyleClass().add("btn-secondary");
        btnAccueil.setOnAction(e -> showHomeScreen());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPanier = new Button(bundle.getString("menu.cart"));
        btnPanier.getStyleClass().add("btn-start");
        btnPanier.setStyle("-fx-font-size: 22px; -fx-padding: 10 30;");
        btnPanier.setOnAction(e -> showCartScreen());

        footer.getChildren().addAll(btnAccueil, spacer, btnPanier);
        menuLayout.setBottom(footer);

        mainLayout.setCenter(menuLayout);
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(20);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(550);
        card.setAlignment(Pos.CENTER_LEFT);

        // Placeholder pour l'image (pourrait être remplacé par une vraie image)
        Rectangle imgPlace = new Rectangle(140, 140, Color.web("#f1f5f9"));
        imgPlace.setArcWidth(20);
        imgPlace.setArcHeight(20);

        // Informations du produit
        VBox info = new VBox(10);
        HBox.setHgrow(info, Priority.ALWAYS);
        info.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(product.getName());
        name.getStyleClass().add("h2");
        name.setStyle("-fx-font-size: 24px;");

        Label desc = new Label(product.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #64748b;");

        // Indicateurs épicé/végétarien (optionnels)
        HBox indicators = new HBox(10);
        if (product.isSpicy()) {
            Label spicyLabel = new Label("🌶️ Épicé");
            spicyLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 14px;");
            indicators.getChildren().add(spicyLabel);
        }
        if (product.isVegetarian()) {
            Label vegLabel = new Label("🥬 Végétarien");
            vegLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 14px;");
            indicators.getChildren().add(vegLabel);
        }

        // Ligne inférieure avec prix et bouton
        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button(bundle.getString("menu.add"));
        btnAdd.getStyleClass().add("btn-add-product");
        btnAdd.setOnAction(e -> showDetailScreen(product));

        bottomRow.getChildren().addAll(price, spacer, btnAdd);

        // Assemblage des éléments
        if (indicators.getChildren().isEmpty()) {
            info.getChildren().addAll(name, desc, bottomRow);
        } else {
            info.getChildren().addAll(name, desc, indicators, bottomRow);
        }

        card.getChildren().addAll(imgPlace, info);
        return card;
    }

    // --- 3. ÉCRAN DE DÉTAIL DU PRODUIT ---
    private void showDetailScreen(Product product) {
        BorderPane detailLayout = new BorderPane();
        detailLayout.setPadding(new Insets(20));

        // Bouton retour
        HBox top = new HBox();
        Button backBtn = new Button(bundle.getString("detail.back"));
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> showMenuScreen());
        top.getChildren().add(backBtn);
        detailLayout.setTop(top);

        // Contenu central
        HBox center = new HBox(60);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        // Image du produit
        Rectangle bigImg = new Rectangle(500, 400, Color.web("#f1f5f9"));
        bigImg.setArcWidth(30);
        bigImg.setArcHeight(30);

        // Informations du produit
        VBox infoCol = new VBox(25);
        infoCol.setPrefWidth(500);

        Label name = new Label(product.getName());
        name.getStyleClass().add("title-large");
        name.setStyle("-fx-font-size: 48px;");

        Label desc = new Label(product.getDescription());
        desc.setStyle("-fx-font-size: 22px; -fx-text-fill: #64748b;");
        desc.setWrapText(true);

        // Indicateurs
        HBox productIndicators = new HBox(20);
        if (product.isSpicy()) {
            Label spicyLabel = new Label("🌶️ Ce plat est épicé");
            spicyLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 18px;");
            productIndicators.getChildren().add(spicyLabel);
        }
        if (product.isVegetarian()) {
            Label vegLabel = new Label("🥬 Plat végétarien");
            vegLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 18px;");
            productIndicators.getChildren().add(vegLabel);
        }

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");
        price.setStyle("-fx-font-size: 32px;");

        // Options personnalisables
        VBox optionsBox = new VBox(20);
        optionsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,0);");

        Label lblOpt1 = new Label(bundle.getString("detail.spice"));
        lblOpt1.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        ToggleGroup groupSpice = new ToggleGroup();
        RadioButton rb1 = new RadioButton(bundle.getString("detail.spice.mild"));
        rb1.setToggleGroup(groupSpice);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton(bundle.getString("detail.spice.medium"));
        rb2.setToggleGroup(groupSpice);

        RadioButton rb3 = new RadioButton(bundle.getString("detail.spice.hot"));
        rb3.setToggleGroup(groupSpice);

        HBox boxSpice = new HBox(20, rb1, rb2, rb3);

        Label lblOpt2 = new Label(bundle.getString("detail.side"));
        lblOpt2.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        ToggleGroup groupSide = new ToggleGroup();
        RadioButton rbRice = new RadioButton(bundle.getString("detail.side.rice"));
        rbRice.setToggleGroup(groupSide);
        rbRice.setSelected(true);

        RadioButton rbNoodle = new RadioButton(bundle.getString("detail.side.noodle"));
        rbNoodle.setToggleGroup(groupSide);

        HBox boxSide = new HBox(20, rbRice, rbNoodle);

        optionsBox.getChildren().addAll(lblOpt1, boxSpice, new Separator(), lblOpt2, boxSide);

        // Actions : quantité et ajout au panier
        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER_LEFT);

        Spinner<Integer> spinner = new Spinner<>(1, 10, 1);
        spinner.setStyle("-fx-font-size: 20px;");
        spinner.setPrefHeight(50);
        spinner.setPrefWidth(100);

        Button btnAddCart = new Button(bundle.getString("detail.add"));
        btnAddCart.getStyleClass().add("btn-start");
        btnAddCart.setStyle("-fx-font-size: 22px; -fx-padding: 10 40;");

        btnAddCart.setOnAction(e -> {
            List<String> options = new ArrayList<>();
            options.add(((RadioButton) groupSpice.getSelectedToggle()).getText());
            options.add(((RadioButton) groupSide.getSelectedToggle()).getText());

            cartService.addProduct(product, spinner.getValue(), options);
            showMenuScreen();
        });

        actions.getChildren().addAll(spinner, btnAddCart);

        // Assemblage des éléments
        infoCol.getChildren().addAll(name, desc);
        if (!productIndicators.getChildren().isEmpty()) {
            infoCol.getChildren().add(productIndicators);
        }
        infoCol.getChildren().addAll(price, optionsBox, actions);

        center.getChildren().addAll(bigImg, infoCol);
        detailLayout.setCenter(center);

        mainLayout.setCenter(detailLayout);
    }

    // --- 4. ÉCRAN DU PANIER ---
    private void showCartScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 40, 20, 40));  // Padding réduit
        root.setAlignment(Pos.TOP_CENTER);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(bundle.getString("cart.title"));
        title.getStyleClass().add("h1");

        Label itemCount = new Label("(" + cartService.getItems().size() + " article(s))");
        itemCount.setStyle("-fx-text-fill: #64748b; -fx-font-size: 18px;");

        header.getChildren().addAll(title, itemCount);

        if (cartService.getItems().isEmpty()) {
            // Panier vide
            VBox emptyCart = new VBox(30);
            emptyCart.setAlignment(Pos.CENTER);
            emptyCart.setPadding(new Insets(100, 0, 0, 0));

            Label emptyLabel = new Label("🛒 Votre panier est vide");
            emptyLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #64748b;");

            Label suggestion = new Label("Parcourez notre menu pour ajouter des plats !");
            suggestion.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

            Button backToMenu = new Button("← Retour au menu");
            backToMenu.getStyleClass().add("btn-primary");
            backToMenu.setStyle("-fx-font-size: 18px; -fx-padding: 12 30;");
            backToMenu.setOnAction(e -> showMenuScreen());

            emptyCart.getChildren().addAll(emptyLabel, suggestion, backToMenu);
            root.getChildren().addAll(header, emptyCart);

        } else {
            // Liste des articles avec possibilité de modifier
            VBox itemsList = new VBox(15);

            for (int i = 0; i < cartService.getItems().size(); i++) {
                CartItem item = cartService.getItems().get(i);
                itemsList.getChildren().add(createCartItemRow(item, i));
            }

            // Séparateur
            Separator separator = new Separator();

            // Total
            HBox totalBox = new HBox(20);
            totalBox.setAlignment(Pos.CENTER_RIGHT);
            totalBox.setPadding(new Insets(20, 0, 0, 0));

            Label totalLabel = new Label("Total à payer : ");
            totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            Label totalValue = new Label(String.format("%.2f €", cartService.getTotal()));
            totalValue.getStyleClass().add("title-large");
            totalValue.setStyle("-fx-font-size: 36px; -fx-text-fill: #d97706;");

            totalBox.getChildren().addAll(totalLabel, totalValue);

            // Informations client
            VBox clientBox = new VBox(10);
            clientBox.setPadding(new Insets(20, 0, 0, 0));

            Label clientLabel = new Label("👤 " + bundle.getString("cart.client_placeholder"));
            clientLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            TextField txtClient = new TextField();
            txtClient.setPromptText("Votre nom...");
            txtClient.setStyle("-fx-font-size: 18px; -fx-padding: 12; -fx-background-radius: 8;");
            txtClient.setPrefWidth(400);

            clientBox.getChildren().addAll(clientLabel, txtClient);

            // Boutons
            HBox buttons = new HBox(30);
            buttons.setAlignment(Pos.CENTER);
            buttons.setPadding(new Insets(40, 0, 0, 0));

            Button continueBtn = new Button("← Continuer mes achats");
            continueBtn.getStyleClass().add("btn-secondary");
            continueBtn.setStyle("-fx-font-size: 18px; -fx-padding: 12 30;");
            continueBtn.setOnAction(e -> showMenuScreen());

            Button validateBtn = new Button("✅ " + bundle.getString("cart.validate"));
            validateBtn.getStyleClass().add("btn-start");
            validateBtn.setStyle("-fx-font-size: 20px; -fx-padding: 12 50;");

            validateBtn.setOnAction(e -> {
                String clientName = txtClient.getText().trim();
                if (clientName.isEmpty()) {
                    showAlert("Veuillez saisir votre nom pour la commande.");
                    txtClient.setStyle("-fx-border-color: #dc2626; -fx-border-width: 2;");
                    return;
                }

                // Log pour debug
                System.out.println("🎯 Commande validée !");
                System.out.println("👤 Client: " + clientName);
                System.out.println("📦 Articles: " + cartService.getItems().size());
                System.out.println("💰 Total: " + cartService.getTotal() + "€");

                // Pour l'instant, simulation
                int orderId = (int) (Math.random() * 9000) + 1000;
                showConfirmationScreen(orderId);
            });

            buttons.getChildren().addAll(continueBtn, validateBtn);

            // Assemblage
            root.getChildren().addAll(header, itemsList, separator, totalBox, clientBox, buttons);
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainLayout.setCenter(scroll);
    }

    // Nouvelle méthode pour créer une ligne d'article INTERACTIVE
    private HBox createCartItemRow(CartItem item, int index) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,0);");

        // Image placeholder
        Rectangle imgPlace = new Rectangle(80, 80, Color.web("#f1f5f9"));
        imgPlace.setArcWidth(15);
        imgPlace.setArcHeight(15);

        // Informations produit
        VBox productInfo = new VBox(5);
        productInfo.setPrefWidth(300);

        Label name = new Label(item.getProduct().getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        // Affiche "Aucune option" si la liste est vide
        String optionsText = item.getOptions().isEmpty() ?
                "Aucune option" : "Options: " + String.join(", ", item.getOptions());
        Label options = new Label(optionsText);
        options.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        productInfo.getChildren().addAll(name, options);

        // Contrôle quantité
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);

        Button minusBtn = new Button("-");
        minusBtn.setStyle("-fx-background-color: #e2e8f0; -fx-min-width: 35; -fx-min-height: 35; " +
                "-fx-background-radius: 17; -fx-font-weight: bold; -fx-font-size: 16px;");
        minusBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                showCartScreen(); // Rafraîchit l'écran
            } else {
                // Supprime si quantité devient 0
                cartService.removeItem(index);
                showCartScreen();
            }
        });

        Label quantityLabel = new Label("×" + item.getQuantity());
        quantityLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-min-width: 50;");

        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color: #e2e8f0; -fx-min-width: 35; -fx-min-height: 35; " +
                "-fx-background-radius: 17; -fx-font-weight: bold; -fx-font-size: 16px;");
        plusBtn.setOnAction(e -> {
            item.setQuantity(item.getQuantity() + 1);
            showCartScreen(); // Rafraîchit l'écran
        });

        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);

        // Prix
        Label price = new Label(String.format("%.2f €", item.getTotalPrice()));
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-min-width: 100;");

        // Bouton supprimer
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; " +
                "-fx-min-width: 45; -fx-min-height: 45; -fx-background-radius: 22; " +
                "-fx-font-size: 18px;");
        deleteBtn.setOnAction(e -> {
            cartService.removeItem(index);
            showCartScreen(); // Rafraîchit l'écran
        });

        row.getChildren().addAll(imgPlace, productInfo, quantityBox, price, deleteBtn);
        return row;
    }
    // --- 5. ÉCRAN DE CONFIRMATION ---
    private void showConfirmationScreen(int orderId) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 100px;");

        Label title = new Label(bundle.getString("confirm.title"));
        title.getStyleClass().add("title-large");

        Label message = new Label(bundle.getString("confirm.msg"));
        message.getStyleClass().add("subtitle");

        Label orderNumber = new Label("#" + orderId);
        orderNumber.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-text-fill: #d97706;");

        Label waitMessage = new Label(bundle.getString("confirm.wait"));
        waitMessage.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20px;");

        Button btnNew = new Button(bundle.getString("confirm.new"));
        btnNew.getStyleClass().add("btn-secondary");
        btnNew.setOnAction(e -> {
            cartService.clear();
            showHomeScreen();
        });

        root.getChildren().addAll(icon, title, message, orderNumber, waitMessage, btnNew);
        mainLayout.setCenter(root);
    }

    // --- MÉTHODES UTILITAIRES ---

    private void showErrorScreen(String errorMessage) {
        VBox errorBox = new VBox(30);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(50));

        Label errorIcon = new Label("⚠️");
        errorIcon.setStyle("-fx-font-size: 60px;");

        Label errorLabel = new Label("Erreur de connexion");
        errorLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

        Label errorDetail = new Label(errorMessage);
        errorDetail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");
        errorDetail.setWrapText(true);
        errorDetail.setMaxWidth(400);

        Button retryBtn = new Button("Réessayer");
        retryBtn.getStyleClass().add("btn-primary");
        retryBtn.setOnAction(e -> showMenuScreen());

        Button backBtn = new Button("Retour à l'accueil");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> showHomeScreen());

        errorBox.getChildren().addAll(errorIcon, errorLabel, errorDetail, retryBtn, backBtn);
        mainLayout.setCenter(errorBox);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}