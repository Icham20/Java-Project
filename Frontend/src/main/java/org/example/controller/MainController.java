package org.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.model.CartItem;
import org.example.model.Category;
import org.example.model.Product;
import org.example.service.ApiService;
import org.example.service.CartService;

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

    // --- 1. ACCUEIL ---
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

    // --- 2. MENU ---
    private void showMenuScreen() {
        BorderPane menuLayout = new BorderPane();
        menuLayout.setPadding(new Insets(20, 40, 20, 40));

        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label lblMenu = new Label(bundle.getString("menu.title"));
        lblMenu.getStyleClass().add("menu-title-orange");

        HBox tabs = new HBox(10);
        tabs.setAlignment(Pos.CENTER);
        HBox.setHgrow(tabs, Priority.ALWAYS);

        List<Category> categories = apiService.getCategories();
        if(categories.isEmpty()) mockCategories(categories);
        if(currentCategory == null && !categories.isEmpty()) currentCategory = categories.get(0);

        for (Category cat : categories) {
            Button tab = new Button(cat.getName());
            tab.getStyleClass().add("tab-button");
            if (currentCategory != null && currentCategory.getId().equals(cat.getId())) {
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

        TilePane grid = new TilePane();
        grid.setHgap(30); grid.setVgap(30);
        grid.setPrefColumns(2);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(20));

        List<Product> products = apiService.getProductsByCategory(currentCategory.getId());
        if(products.isEmpty()) mockProducts(products, currentCategory.getId());

        for (Product p : products) {
            grid.getChildren().add(createProductCard(p));
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        menuLayout.setCenter(scroll);

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

    private HBox createProductCard(Product p) {
        HBox card = new HBox(20);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(550);
        card.setAlignment(Pos.CENTER_LEFT);

        Rectangle imgPlace = new Rectangle(140, 140, Color.web("#f1f5f9"));
        imgPlace.setArcWidth(20); imgPlace.setArcHeight(20);

        VBox info = new VBox(10);
        HBox.setHgrow(info, Priority.ALWAYS);
        info.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(p.getName());
        name.getStyleClass().add("h2");
        name.setStyle("-fx-font-size: 24px;");

        Label desc = new Label(p.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #64748b;");

        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label price = new Label(String.format("%.2f €", p.getPrice()));
        price.getStyleClass().add("price-text");

        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);

        Button btnAdd = new Button(bundle.getString("menu.add"));
        btnAdd.getStyleClass().add("btn-add-product");
        btnAdd.setOnAction(e -> showDetailScreen(p));

        bottomRow.getChildren().addAll(price, r, btnAdd);
        info.getChildren().addAll(name, desc, bottomRow);

        card.getChildren().addAll(imgPlace, info);
        return card;
    }

    // --- 3. DÉTAIL ---
    private void showDetailScreen(Product p) {
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

        Rectangle bigImg = new Rectangle(500, 400, Color.web("#f1f5f9"));
        bigImg.setArcWidth(30); bigImg.setArcHeight(30);

        VBox infoCol = new VBox(25);
        infoCol.setPrefWidth(500);

        Label name = new Label(p.getName());
        name.getStyleClass().add("title-large");
        name.setStyle("-fx-font-size: 48px;");

        Label desc = new Label(p.getDescription());
        desc.setStyle("-fx-font-size: 22px; -fx-text-fill: #64748b;");
        desc.setWrapText(true);

        Label price = new Label(String.format("%.2f €", p.getPrice()));
        price.getStyleClass().add("price-text");
        price.setStyle("-fx-font-size: 32px;");

        VBox optionsBox = new VBox(20);
        optionsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,0);");

        Label lblOpt1 = new Label(bundle.getString("detail.spice"));
        lblOpt1.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        ToggleGroup groupSpice = new ToggleGroup();
        RadioButton rb1 = new RadioButton(bundle.getString("detail.spice.mild")); rb1.setToggleGroup(groupSpice); rb1.setSelected(true);
        RadioButton rb2 = new RadioButton(bundle.getString("detail.spice.medium")); rb2.setToggleGroup(groupSpice);
        RadioButton rb3 = new RadioButton(bundle.getString("detail.spice.hot")); rb3.setToggleGroup(groupSpice);
        HBox boxSpice = new HBox(20, rb1, rb2, rb3);

        Label lblOpt2 = new Label(bundle.getString("detail.side"));
        lblOpt2.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        ToggleGroup groupSide = new ToggleGroup();
        RadioButton rbRice = new RadioButton(bundle.getString("detail.side.rice")); rbRice.setToggleGroup(groupSide); rbRice.setSelected(true);
        RadioButton rbNoodle = new RadioButton(bundle.getString("detail.side.noodle")); rbNoodle.setToggleGroup(groupSide);
        HBox boxSide = new HBox(20, rbRice, rbNoodle);

        optionsBox.getChildren().addAll(lblOpt1, boxSpice, new Separator(), lblOpt2, boxSide);

        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER_LEFT);

        Spinner<Integer> spinner = new Spinner<>(1, 10, 1);
        spinner.setStyle("-fx-font-size: 20px; -fx-body-color: white;");
        spinner.setPrefHeight(50);
        spinner.setPrefWidth(100);

        Button btnAddCart = new Button(bundle.getString("detail.add"));
        btnAddCart.getStyleClass().add("btn-start");
        btnAddCart.setStyle("-fx-font-size: 22px; -fx-padding: 10 40;");

        btnAddCart.setOnAction(e -> {
            List<String> opts = new ArrayList<>();
            opts.add(((RadioButton)groupSpice.getSelectedToggle()).getText());
            opts.add(((RadioButton)groupSide.getSelectedToggle()).getText());
            cartService.addProduct(p, spinner.getValue(), opts);
            showMenuScreen();
        });

        actions.getChildren().addAll(spinner, btnAddCart);

        infoCol.getChildren().addAll(name, desc, price, optionsBox, actions);
        center.getChildren().addAll(bigImg, infoCol);
        detailLayout.setCenter(center);

        mainLayout.setCenter(detailLayout);
    }

    // --- 4. PANIER ---
    private void showCartScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(50, 200, 50, 200));
        root.setAlignment(Pos.TOP_CENTER);

        VBox receipt = new VBox(20);
        receipt.getStyleClass().add("receipt-box");

        Label title = new Label(bundle.getString("cart.title"));
        title.getStyleClass().add("h1");
        title.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15);
        grid.add(new Label(bundle.getString("cart.article")), 0, 0);
        grid.add(new Label(bundle.getString("cart.qty")), 1, 0);
        grid.add(new Label(bundle.getString("cart.total")), 2, 0);
        grid.getChildren().forEach(n -> n.setStyle("-fx-font-weight:bold; -fx-text-fill: #94a3b8;"));

        int row = 1;
        for (CartItem item : cartService.getItems()) {
            VBox itemDesc = new VBox(2);
            Label name = new Label(item.getProduct().getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            Label opts = new Label(String.join(", ", item.getOptions()));
            opts.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
            itemDesc.getChildren().addAll(name, opts);

            Label qty = new Label("x" + item.getQuantity());
            qty.setStyle("-fx-font-size: 18px;");

            Label price = new Label(String.format("%.2f €", item.getTotalPrice()));
            price.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            grid.add(itemDesc, 0, row);
            grid.add(qty, 1, row);
            grid.add(price, 2, row);
            row++;
        }

        Separator sep = new Separator();

        HBox totalBox = new HBox(20);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label lblTotal = new Label(bundle.getString("cart.total_pay"));
        Label valTotal = new Label(String.format("%.2f €", cartService.getTotal()));
        valTotal.getStyleClass().add("title-large");
        valTotal.setStyle("-fx-font-size: 40px; -fx-text-fill: #d97706;");
        totalBox.getChildren().addAll(lblTotal, valTotal);

        VBox clientBox = new VBox(10);
        Label lblClient = new Label(bundle.getString("cart.client_placeholder"));
        lblClient.setStyle("-fx-text-fill: #64748b;");
        TextField txtClient = new TextField();
        txtClient.setStyle("-fx-font-size: 18px; -fx-padding: 10; -fx-background-radius: 8;");
        clientBox.getChildren().addAll(lblClient, txtClient);

        receipt.getChildren().addAll(title, new Separator(), grid, sep, totalBox, clientBox);

        HBox actions = new HBox(40);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(30,0,0,0));

        Button btnBack = new Button(bundle.getString("cart.modify"));
        btnBack.getStyleClass().add("btn-secondary");
        btnBack.setOnAction(e -> showMenuScreen());

        Button btnPay = new Button(bundle.getString("cart.validate"));
        btnPay.getStyleClass().add("btn-start");
        btnPay.setOnAction(e -> showConfirmationScreen(1234));

        actions.getChildren().addAll(btnBack, btnPay);

        root.getChildren().addAll(receipt, actions);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainLayout.setCenter(scroll);
    }

    // --- 5. CONFIRMATION ---
    private void showConfirmationScreen(int orderId) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 100px;");

        Label title = new Label(bundle.getString("confirm.title"));
        title.getStyleClass().add("title-large");

        Label msg = new Label(bundle.getString("confirm.msg"));
        msg.getStyleClass().add("subtitle");

        Label numCmd = new Label("#" + orderId);
        numCmd.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-text-fill: #d97706;");

        Label waitMsg = new Label(bundle.getString("confirm.wait"));
        waitMsg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20px;");

        Button btnNew = new Button(bundle.getString("confirm.new"));
        btnNew.getStyleClass().add("btn-secondary");
        btnNew.setOnAction(e -> {
            cartService.clear();
            showHomeScreen();
        });

        root.getChildren().addAll(icon, title, msg, numCmd, waitMsg, btnNew);
        mainLayout.setCenter(root);
    }

    // --- MOCKS (DONNÉES TRADUITES) ---
    // C'est ici que la magie opère ! On utilise 'bundle.getString'

    private void mockCategories(List<Category> list) {
        list.add(new Category(1L, bundle.getString("cat.1")));
        list.add(new Category(2L, bundle.getString("cat.2")));
        list.add(new Category(3L, bundle.getString("cat.3")));
        list.add(new Category(4L, bundle.getString("cat.4")));
    }

    private void mockProducts(List<Product> list, Long catId) {
        if (catId == 1L) {
            list.add(new Product(10L, bundle.getString("prod.10.name"), 6.90, bundle.getString("prod.10.desc"), catId));
            list.add(new Product(11L, bundle.getString("prod.11.name"), 5.50, bundle.getString("prod.11.desc"), catId));
        } else if (catId == 2L) {
            list.add(new Product(20L, bundle.getString("prod.20.name"), 12.90, bundle.getString("prod.20.desc"), catId));
            list.add(new Product(21L, bundle.getString("prod.21.name"), 14.50, bundle.getString("prod.21.desc"), catId));
            list.add(new Product(22L, bundle.getString("prod.22.name"), 14.90, bundle.getString("prod.22.desc"), catId));
        } else {
            list.add(new Product(30L, bundle.getString("prod.30.name"), 4.50, bundle.getString("prod.30.desc"), catId));
            list.add(new Product(31L, bundle.getString("prod.31.name"), 5.00, bundle.getString("prod.31.desc"), catId));
        }
    }
}