package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.example.model.CartItem;
import org.example.services.CartService;
import org.example.model.Category;
import org.example.model.Product;
import org.example.services.ApiService;

import java.util.*;
import java.util.stream.Collectors;

import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.Scene;






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
        try {
            this.bundle = ResourceBundle.getBundle("org.example.strings", currentLocale, new UTF8Control());
        } catch (Exception e) {
            this.bundle = ResourceBundle.getBundle("org.example.strings", currentLocale);
        }
        showHomeScreen();
    }

    // Classe interne pour forcer l'UTF-8
    public static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, java.io.IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            java.io.InputStream stream = loader.getResourceAsStream(resourceName);
            if (stream != null) {
                try (java.io.InputStreamReader reader = new java.io.InputStreamReader(stream, "UTF-8")) {
                    return new java.util.PropertyResourceBundle(reader);
                }
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }
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

        List<Category> categories = apiService.getCategories();

        if (categories.isEmpty()) {
            showErrorScreen("Impossible de récupérer les catégories depuis le serveur.");
            return;
        }

        if (currentCategory == null) {
            currentCategory = categories.get(0);
        }

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

        // --- GESTION IMAGE ---
        Node imageNode;
        String imagePath = "/org/example/images/" + product.getImageUrl();

        // On vérifie si l'image existe dans les ressources
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty() &&
                getClass().getResource(imagePath) != null) {

            // Si oui, on charge l'image
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imageView.setFitWidth(140);
            imageView.setFitHeight(140);
            imageView.setPreserveRatio(true);

            // On arrondit les coins de l'image
            Rectangle clip = new Rectangle(140, 140);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);

            imageNode = imageView;
        } else {
            // Sinon, on garde le rectangle gris habituel
            Rectangle imgPlace = new Rectangle(140, 140, Color.web("#f1f5f9"));
            imgPlace.setArcWidth(20);
            imgPlace.setArcHeight(20);
            imageNode = imgPlace;
        }

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

        HBox indicators = new HBox(10);
        if (product.isSpicy()) {
            Label spicyLabel = new Label("Épicé");
            spicyLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 14px;");
            indicators.getChildren().add(spicyLabel);
        }

        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button(bundle.getString("menu.add"));
        btnAdd.getStyleClass().add("btn-add-product");

        if (!product.isAvailable()) {
            btnAdd.setText("INDISPONIBLE");
            btnAdd.setDisable(true);
            btnAdd.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-border-color: #dc2626; -fx-font-weight: bold; -fx-opacity: 1;");
            name.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px;");
            desc.setStyle("-fx-text-fill: #cbd5e1;");
        } else {
            btnAdd.setOnAction(e -> showDetailScreen(product));
        }

        bottomRow.getChildren().addAll(price, spacer, btnAdd);

        if (indicators.getChildren().isEmpty()) {
            info.getChildren().addAll(name, desc, bottomRow);
        } else {
            info.getChildren().addAll(name, desc, indicators, bottomRow);
        }

        // On ajoute l'image (ou le rectangle) à la carte
        card.getChildren().addAll(imageNode, info);
        return card;
    }

    // --- 3. ÉCRAN DE DÉTAIL DU PRODUIT ---
    private void showDetailScreen(Product product) {
        BorderPane detailLayout = new BorderPane();
        detailLayout.setPadding(new Insets(20));

        HBox top = new HBox();
        Button backBtn = new Button(bundle.getString("detail.back"));
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> showMenuScreen());
        top.getChildren().add(backBtn);
        detailLayout.setTop(top);

        HBox center = new HBox(60);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        // --- GESTION GRANDE IMAGE ---
        Node imageNode;
        String imagePath = "/org/example/images/" + product.getImageUrl();

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty() &&
                getClass().getResource(imagePath) != null) {

            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imageView.setFitWidth(500);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);

            Rectangle clip = new Rectangle(500, 400);
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            imageView.setClip(clip);

            imageNode = imageView;
        } else {
            Rectangle bigImg = new Rectangle(500, 400, Color.web("#f1f5f9"));
            bigImg.setArcWidth(30);
            bigImg.setArcHeight(30);
            imageNode = bigImg;
        }

        VBox infoCol = new VBox(25);
        infoCol.setPrefWidth(500);

        Label name = new Label(product.getName());
        name.getStyleClass().add("title-large");
        name.setStyle("-fx-font-size: 48px;");

        Label desc = new Label(product.getDescription());
        desc.setStyle("-fx-font-size: 22px; -fx-text-fill: #64748b;");
        desc.setWrapText(true);

        HBox productIndicators = new HBox(20);
        if (product.isSpicy()) {
            Label spicyLabel = new Label("Épicé");
            spicyLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 18px;");
            productIndicators.getChildren().add(spicyLabel);
        }

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");
        price.setStyle("-fx-font-size: 32px;");

        VBox optionsBox = new VBox(20);
        optionsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,0);");

        java.util.function.Supplier<List<String>> optionsSupplier;
        long catId = product.getCategoryId();

        if (catId == 2) { // PLATS
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

            optionsSupplier = () -> {
                List<String> opts = new ArrayList<>();
                opts.add(((RadioButton) groupSpice.getSelectedToggle()).getText());
                opts.add(((RadioButton) groupSide.getSelectedToggle()).getText());
                return opts;
            };

        } else if (catId == 3) { // DESSERTS
            Label lblCoffee = new Label(bundle.getString("detail.coffee"));
            lblCoffee.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            ToggleGroup groupCoffee = new ToggleGroup();
            RadioButton rbYes = new RadioButton(bundle.getString("detail.yes"));
            rbYes.setToggleGroup(groupCoffee);
            RadioButton rbNo = new RadioButton(bundle.getString("detail.no"));
            rbNo.setToggleGroup(groupCoffee);
            rbNo.setSelected(true);

            HBox boxCoffee = new HBox(20, rbYes, rbNo);
            optionsBox.getChildren().addAll(lblCoffee, boxCoffee);

            optionsSupplier = () -> {
                List<String> opts = new ArrayList<>();
                if (rbYes.isSelected()) {
                    opts.add("Avec Café");
                }
                return opts;
            };

        } else if (catId == 4) { // BOISSONS
            Label lblIce = new Label(bundle.getString("detail.ice"));
            lblIce.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            ToggleGroup groupIce = new ToggleGroup();
            RadioButton rbIceYes = new RadioButton(bundle.getString("detail.yes"));
            rbIceYes.setToggleGroup(groupIce);
            rbIceYes.setSelected(true);

            RadioButton rbIceNo = new RadioButton(bundle.getString("detail.no"));
            rbIceNo.setToggleGroup(groupIce);

            HBox boxIce = new HBox(20, rbIceYes, rbIceNo);
            optionsBox.getChildren().addAll(lblIce, boxIce);

            optionsSupplier = () -> {
                List<String> opts = new ArrayList<>();
                if (rbIceYes.isSelected()) {
                    opts.add("Avec Glaçons");
                } else {
                    opts.add("Sans Glaçons");
                }
                return opts;
            };

        } else { // ENTRÉES
            optionsSupplier = ArrayList::new;
            optionsBox.setVisible(false);
            optionsBox.setManaged(false);
        }

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
            List<String> options = optionsSupplier.get();
            cartService.addProduct(product, spinner.getValue(), options);

            // Afficher les suggestions intelligentes après l'ajout
            showSmartSuggestionsAfterAdding(product);

            // Retour au menu avec un petit délai
            PauseTransition delay = new PauseTransition(Duration.millis(100));
            delay.setOnFinished(event -> showMenuScreen());
            delay.play();
        });

        actions.getChildren().addAll(spinner, btnAddCart);

        infoCol.getChildren().addAll(name, desc);
        if (!productIndicators.getChildren().isEmpty()) {
            infoCol.getChildren().add(productIndicators);
        }
        infoCol.getChildren().addAll(price, optionsBox, actions);

        // Ajout de l'image (ou rectangle) au centre
        center.getChildren().addAll(imageNode, infoCol);
        detailLayout.setCenter(center);

        mainLayout.setCenter(detailLayout);
    }

    // --- 4. ÉCRAN DU PANIER ---
    private void showCartScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 40, 20, 40));
        root.setAlignment(Pos.TOP_CENTER);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(bundle.getString("cart.title"));
        title.getStyleClass().add("h1");

        Label itemCount = new Label("(" + cartService.getItems().size() + " article(s))");
        itemCount.setStyle("-fx-text-fill: #64748b; -fx-font-size: 18px;");

        header.getChildren().addAll(title, itemCount);

        if (cartService.getItems().isEmpty()) {
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
            VBox itemsList = new VBox(15);

            for (int i = 0; i < cartService.getItems().size(); i++) {
                CartItem item = cartService.getItems().get(i);
                itemsList.getChildren().add(createCartItemRow(item, i));
            }

            Separator separator = new Separator();

            HBox totalBox = new HBox(20);
            totalBox.setAlignment(Pos.CENTER_RIGHT);
            totalBox.setPadding(new Insets(20, 0, 0, 0));

            Label totalLabel = new Label("Total à payer : ");
            totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            Label totalValue = new Label(String.format("%.2f €", cartService.getTotal()));
            totalValue.getStyleClass().add("title-large");
            totalValue.setStyle("-fx-font-size: 36px; -fx-text-fill: #d97706;");

            totalBox.getChildren().addAll(totalLabel, totalValue);

            VBox clientBox = new VBox(10);
            clientBox.setPadding(new Insets(20, 0, 0, 0));

            Label clientLabel = new Label("👤 " + bundle.getString("cart.client_placeholder"));
            clientLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            TextField txtClient = new TextField();
            txtClient.setPromptText("Votre nom...");
            txtClient.setStyle("-fx-font-size: 18px; -fx-padding: 12; -fx-background-radius: 8;");
            txtClient.setPrefWidth(400);

            clientBox.getChildren().addAll(clientLabel, txtClient);

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

                // Construction de la commande
                List<org.example.model.OrderItem> orderItems = new ArrayList<>();
                for (CartItem ci : cartService.getItems()) {
                    orderItems.add(new org.example.model.OrderItem(
                            ci.getProduct().getId().intValue(),
                            ci.getQuantity(),
                            ci.getProduct().getPrice(),
                            String.join(",", ci.getOptions())
                    ));
                }

                org.example.model.Order newOrder = new org.example.model.Order(
                        clientName,
                        cartService.getTotal(),
                        orderItems
                );

                int orderId = apiService.createOrder(newOrder);

                if (orderId > 0) {
                    System.out.println("✅ Commande envoyée avec succès : ID " + orderId);
                    showConfirmationScreen(orderId);
                } else {
                    showAlert("Erreur lors de l'envoi de la commande. Stock peut-être insuffisant.");
                }
            });

            buttons.getChildren().addAll(continueBtn, validateBtn);
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

        // Image placeholder (ou image réelle si tu veux l'ajouter ici aussi)
        Rectangle imgPlace = new Rectangle(80, 80, Color.web("#f1f5f9"));
        imgPlace.setArcWidth(15);
        imgPlace.setArcHeight(15);

        // Informations produit
        VBox productInfo = new VBox(5);
        productInfo.setPrefWidth(300);

        Label name = new Label(item.getProduct().getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

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
                showCartScreen();
            } else {
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
            showCartScreen();
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
            showCartScreen();
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

    // Ajoutez cette variable de classe
    private long lastSuggestionTime = 0;
    // Dans MainController, remplacez la méthode showDessertSuggestionsAfterAdding par :
    private boolean suggestionPopupOpen = false;
    private void showSmartSuggestionsAfterAdding(Product justAddedProduct) {
        System.out.println("🟢 Début des suggestions pour: " + justAddedProduct.getName());

        // Vérifier si une popup est déjà ouverte
        if (suggestionPopupOpen) {
            System.out.println("⚠️ Popup déjà ouverte");
            return;
        }

        // Réduire le délai à 1 seconde
        if (System.currentTimeMillis() - lastSuggestionTime < 1000) {
            System.out.println("⚠️ Suggestions trop récentes");
            return;
        }

        Platform.runLater(() -> {
            try {
                // Utiliser la nouvelle méthode limitée
                List<Product> suggestions = cartService.getLimitedSuggestions(justAddedProduct);

                System.out.println("🎯 " + suggestions.size() + " suggestions limitées trouvées:");
                for (Product p : suggestions) {
                    System.out.println("  - " + p.getName() + " (Catégorie: " + p.getCategoryId() + ")");
                }

                if (!suggestions.isEmpty()) {
                    lastSuggestionTime = System.currentTimeMillis();

                    PauseTransition delay = new PauseTransition(Duration.millis(500));
                    delay.setOnFinished(e -> {
                        createLimitedSuggestionPopup(suggestions, justAddedProduct);
                    });
                    delay.play();
                } else {
                    System.out.println("ℹ️ Aucune suggestion disponible");
                }
            } catch (Exception ex) {
                System.err.println("❌ Erreur: " + ex.getMessage());
            }
        });
    }

    // Nouvelle méthode pour créer la popup de suggestions intelligentes
    private void createLimitedSuggestionPopup(List<Product> suggestions, Product mainProduct) {
        if (suggestionPopupOpen) {
            return;
        }

        if (mainLayout.getScene() == null || mainLayout.getScene().getWindow() == null) {
            return;
        }

        suggestionPopupOpen = true;

        Stage popup = new Stage();
        popup.initOwner(mainLayout.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Complétez votre commande !");
        popup.initStyle(StageStyle.UTILITY);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #d97706; -fx-border-width: 3;");

        // Titre principal
        Label title = new Label("✨ Complétez votre " + mainProduct.getName() + " !");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label subtitle = new Label("Ces produits iraient parfaitement avec votre sélection");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        // Grille des suggestions (maximum 3)
        HBox suggestionsGrid = new HBox(20);
        suggestionsGrid.setAlignment(Pos.CENTER);
        suggestionsGrid.setPadding(new Insets(20, 0, 20, 0));

        for (Product product : suggestions) {
            suggestionsGrid.getChildren().add(createSimpleSuggestionCard(product, popup));
        }

        // Bouton "Passer"
        Button skipBtn = new Button("Non merci, je garde ma commande actuelle");
        skipBtn.setStyle("-fx-background-color: #e2e8f0; " +
                "-fx-text-fill: #475569; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 20;");
        skipBtn.setOnAction(e -> {
            suggestionPopupOpen = false;
            popup.close();
        });

        root.getChildren().addAll(title, subtitle, suggestionsGrid, skipBtn);

        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.sizeToScene();

        // Gestion de la fermeture
        popup.setOnHidden(e -> {
            suggestionPopupOpen = false;
        });

        popup.setOnCloseRequest(e -> {
            suggestionPopupOpen = false;
        });

        popup.show();
        popup.centerOnScreen();
    }

    // Méthode pour créer une section de catégorie
    private VBox createCategorySection(String categoryTitle, List<Product> products, Stage popup) {
        VBox categoryBox = new VBox(10);

        // Titre de la catégorie
        Label categoryLabel = new Label(categoryTitle);
        categoryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #d97706;");

        // Grille de produits
        HBox productsBox = new HBox(15);
        productsBox.setAlignment(Pos.CENTER);

        for (Product product : products) {
            productsBox.getChildren().add(createSimpleSuggestionCard(product, popup));
        }

        categoryBox.getChildren().addAll(categoryLabel, productsBox);
        return categoryBox;
    }

    // Modifiez createSimpleDessertCard pour devenir createSuggestionCard
    private VBox createSimpleSuggestionCard(Product product, Stage popup) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180); // Légèrement plus large pour l'image
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #fbbf24; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10; " +
                "-fx-cursor: hand;");

        // Effet hover
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #d97706; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #fbbf24; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 10;");
        });

        // --- IMAGE DU PRODUIT (au lieu de l'emoji) ---
        Node imageNode;
        String imagePath = "/org/example/images/" + product.getImageUrl();

        // Vérifier si l'image existe dans les ressources
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty() &&
                getClass().getResource(imagePath) != null) {

            // Charger l'image réelle
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imageView.setFitWidth(120); // Taille adaptée pour la popup
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);

            // Arrondir les coins de l'image
            Rectangle clip = new Rectangle(120, 120);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);

            imageNode = imageView;
        } else {
            // Fallback : rectangle coloré avec icône
            StackPane imagePlaceholder = new StackPane();
            Rectangle imgPlace = new Rectangle(120, 120, getColorForCategory(product.getCategoryId()));
            imgPlace.setArcWidth(15);
            imgPlace.setArcHeight(15);

            // Icône en overlay
            Label iconLabel = new Label(getIconForCategory(product.getCategoryId()));
            iconLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");

            imagePlaceholder.getChildren().addAll(imgPlace, iconLabel);
            imageNode = imagePlaceholder;
        }

        // Nom (tronqué si trop long)
        String productName = product.getName();
        Label name = new Label(productName);
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        name.setWrapText(true);
        name.setMaxWidth(150);
        name.setTextAlignment(TextAlignment.CENTER);

        // Prix
        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d97706;");

        // Bouton Ajouter
        Button addBtn = new Button("Ajouter");
        addBtn.setStyle("-fx-background-color: #10b981; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand;");

        addBtn.setOnAction(e -> {
            // Options par défaut selon la catégorie
            List<String> options = new ArrayList<>();
            if (product.getCategoryId() == 4L) { // Boisson
                options.add("Avec Glaçons");
            } else if (product.getCategoryId() == 3L) { // Dessert
                options.add("Standard");
            }

            cartService.addProduct(product, 1, options);
            suggestionPopupOpen = false;
            popup.close();
            showQuickNotification("✅ " + product.getName() + " ajouté !");
        });

        card.getChildren().addAll(imageNode, name, price, addBtn);
        return card;
    }

    // Méthode utilitaire pour les icônes
    private String getIconForCategory(Long categoryId) {
        if (categoryId == null) return "📦";

        switch (categoryId.intValue()) {
            case 1: return "🥟"; // Entrées
            case 2: return "🍛"; // Plats
            case 3: return "🍨"; // Desserts
            case 4: return "🥤"; // Boissons
            default: return "📦";
        }
    }
    private Color getColorForCategory(Long categoryId) {
        if (categoryId == null) return Color.web("#94a3b8"); // Gris

        switch (categoryId.intValue()) {
            case 1: return Color.web("#fbbf24"); // Orange pour entrées
            case 2: return Color.web("#ef4444"); // Rouge pour plats
            case 3: return Color.web("#8b5cf6"); // Violet pour desserts
            case 4: return Color.web("#3b82f6"); // Bleu pour boissons
            default: return Color.web("#94a3b8"); // Gris par défaut
        }
    }


    // Méthode utilitaire pour notification rapide
    private void showQuickNotification(String message) {
        Stage notifStage = new Stage();
        notifStage.initStyle(StageStyle.UNDECORATED);
        notifStage.initModality(Modality.NONE);

        VBox notifBox = new VBox(10);
        notifBox.setAlignment(Pos.CENTER);
        notifBox.setPadding(new Insets(20));
        notifBox.setStyle("-fx-background-color: #d1fae5; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #10b981; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;");

        Label notifLabel = new Label(message);
        notifLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #065f46;");

        notifBox.getChildren().add(notifLabel);

        Scene notifScene = new Scene(notifBox);
        notifStage.setScene(notifScene);

        // Positionner en bas à droite
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        notifStage.setX(screenBounds.getWidth() - 350);
        notifStage.setY(screenBounds.getHeight() - 150);

        notifStage.show();

        // Fermer automatiquement après 2 secondes
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> notifStage.close());
        delay.play();
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