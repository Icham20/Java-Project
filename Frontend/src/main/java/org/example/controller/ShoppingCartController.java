package org.example.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.model.CartItem;
import org.example.services.ApiService;
import org.example.services.CartService;
import org.example.utils.InterfaceTools;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur responsable de l'écran Panier et de la validation de commande.
 */
public class ShoppingCartController {

    private final CartService cartService;
    private final ApiService apiService;
    private final MainAppController mainController;

    public ShoppingCartController(CartService cartService, ApiService apiService, MainAppController mainController) {
        this.cartService = cartService;
        this.apiService = apiService;
        this.mainController = mainController;
    }

    /** Affiche l'écran principal du panier (Liste, Total, Formulaire client). */
    public void showCartScreen(BorderPane mainLayout, ResourceBundle bundle) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        VBox cartContainer = new VBox(20);
        cartContainer.getStyleClass().add("cart-container");
        cartContainer.setMaxWidth(850);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(bundle.getString("cart.title"));
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Gestion pluriel "Article(s)"
        int count = cartService.getItems().size();
        String articleWord = bundle.getString("cart.article");
        if (count > 1) { articleWord += "s"; }

        Label itemCount = new Label(count + " " + articleWord);
        itemCount.setStyle("-fx-text-fill: #64748b; -fx-font-size: 18px; -fx-background-color: #f1f5f9; -fx-padding: 5 15; -fx-background-radius: 20;");
        header.getChildren().addAll(title, itemCount);

        if (cartService.getItems().isEmpty()) {
            // Affichage panier vide
            VBox emptyBox = new VBox(20);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50, 0, 50, 0));
            Label emptyEmoji = new Label("🛒");
            emptyEmoji.setStyle("-fx-font-size: 60px;");
            Label emptyText = new Label(bundle.getString("cart.empty"));
            emptyText.setStyle("-fx-font-size: 24px; -fx-text-fill: #64748b; -fx-font-weight: bold;");
            Label suggestion = new Label(bundle.getString("cart.empty.suggestion"));
            suggestion.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");
            Button backBtn = new Button(bundle.getString("menu.back"));
            backBtn.getStyleClass().add("btn-primary");
            backBtn.setOnAction(e -> mainController.showMenuScreen());
            emptyBox.getChildren().addAll(emptyEmoji, emptyText, suggestion, backBtn);
            cartContainer.getChildren().addAll(header, new Separator(), emptyBox);
        } else {
            // Liste des articles
            VBox itemsList = new VBox(15);
            ScrollPane listScroll = new ScrollPane(itemsList);
            listScroll.setFitToWidth(true);
            listScroll.setPrefHeight(400);
            listScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            listScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            for (int i = 0; i < cartService.getItems().size(); i++) {
                itemsList.getChildren().add(createCartItemRow(cartService.getItems().get(i), i, bundle));
            }

            Separator separator = new Separator();
            HBox totalBox = new HBox(10);
            totalBox.setAlignment(Pos.CENTER_RIGHT);
            Label totalLabel = new Label(bundle.getString("cart.total_pay"));
            totalLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b;");
            Label totalValue = new Label(String.format("%.2f €", cartService.getTotal()));
            totalValue.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #d97706;");
            totalBox.getChildren().addAll(totalLabel, totalValue);

            VBox clientBox = new VBox(8);
            clientBox.setAlignment(Pos.CENTER);
            Label lblClient = new Label(bundle.getString("cart.client_label"));
            lblClient.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 18px;");
            TextField txtClient = new TextField();
            txtClient.setPromptText(bundle.getString("cart.client_placeholder"));
            txtClient.getStyleClass().add("cart-input");
            txtClient.setMaxWidth(500);
            clientBox.getChildren().addAll(lblClient, txtClient);

            HBox actions = new HBox(20);
            actions.setAlignment(Pos.CENTER);
            actions.setPadding(new Insets(20, 0, 0, 0));
            Button btnBack = new Button(bundle.getString("cart.continue"));
            btnBack.getStyleClass().add("btn-secondary");
            btnBack.setOnAction(e -> mainController.showMenuScreen());
            Button btnPay = new Button(bundle.getString("cart.validate"));
            btnPay.getStyleClass().add("btn-validate");

            // Action Payer
            btnPay.setOnAction(e -> {
                String clientName = txtClient.getText().trim();
                if (clientName.isEmpty()) { InterfaceTools.showAlert("Veuillez saisir votre nom."); txtClient.setStyle("-fx-border-color: #dc2626; -fx-border-width: 2;"); return; }
                List<org.example.model.OrderItem> orderItems = new ArrayList<>();
                for (CartItem ci : cartService.getItems()) { orderItems.add(new org.example.model.OrderItem(ci.getProduct().getId().intValue(), ci.getQuantity(), ci.getProduct().getPrice(), String.join(",", ci.getOptions()))); }
                org.example.model.Order newOrder = new org.example.model.Order(clientName, cartService.getTotal(), orderItems);
                int orderId = apiService.createOrder(newOrder);
                if (orderId > 0) { showConfirmationScreen(orderId, bundle); } else { InterfaceTools.showAlert("Erreur lors de l'envoi."); }
            });

            actions.getChildren().addAll(btnBack, btnPay);
            cartContainer.getChildren().addAll(header, new Separator(), listScroll, separator, totalBox, clientBox, actions);
        }

        root.getChildren().add(cartContainer);
        ScrollPane mainScroll = new ScrollPane(root);
        mainScroll.setFitToWidth(true);
        mainScroll.setFitToHeight(true);
        mainScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainLayout.setCenter(mainScroll);
    }

    /** Crée une ligne pour un article (Image, Nom, Qté, Supprimer). */
    private HBox createCartItemRow(CartItem item, int index, ResourceBundle bundle) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("cart-row");

        row.getChildren().add(InterfaceTools.createProductImageNode(item.getProduct().getImageUrl(), 80, 80));

        VBox productInfo = new VBox(5);
        productInfo.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(mainController.getTranslateName(item.getProduct()));
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #1e293b;");
        Label options = new Label(String.join(", ", item.getOptions()));
        options.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-style: italic;");
        productInfo.getChildren().addAll(name, options);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox quantityBox = new HBox(15);
        quantityBox.setAlignment(Pos.CENTER);
        quantityBox.getStyleClass().add("quantity-box");
        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("btn-quantity");
        minusBtn.setOnAction(e -> { if (item.getQuantity() > 1) { item.setQuantity(item.getQuantity() - 1); showCartScreen(mainController.getMainLayout(), bundle); } else { cartService.removeItem(index); showCartScreen(mainController.getMainLayout(), bundle); } });
        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.getStyleClass().add("label-quantity");
        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("btn-quantity");
        plusBtn.setOnAction(e -> { item.setQuantity(item.getQuantity() + 1); showCartScreen(mainController.getMainLayout(), bundle); });
        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);

        Label price = new Label(String.format("%.2f €", item.getTotalPrice()));
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #d97706; -fx-min-width: 90; -fx-alignment: center-right;");
        Button deleteBtn = new Button("🗑");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 24px; -fx-cursor: hand; -fx-padding: 0 10 0 10;");
        deleteBtn.setOnAction(e -> { cartService.removeItem(index); showCartScreen(mainController.getMainLayout(), bundle); });

        row.getChildren().addAll(productInfo, spacer, quantityBox, price, deleteBtn);
        return row;
    }

    /** Affiche l'écran de confirmation de commande. */
    private void showConfirmationScreen(int orderId, ResourceBundle bundle) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0fdf4;"); // Fond vert très clair

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 100px;");

        Label title = new Label(bundle.getString("confirm.title"));
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #16a34a;"); // Vert

        Label message = new Label(bundle.getString("confirm.msg"));
        message.setStyle("-fx-font-size: 24px; -fx-text-fill: #475569; -fx-font-weight: bold;");

        Label orderNumber = new Label("#" + orderId);
        orderNumber.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-text-fill: #16a34a;"); // Vert aussi

        Label waitMessage = new Label(bundle.getString("confirm.wait"));
        waitMessage.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20px; -fx-font-style: italic;");

        Button btnNew = new Button(bundle.getString("confirm.new"));
        btnNew.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px; -fx-padding: 15 40; -fx-background-radius: 12; -fx-cursor: hand;");

        // Effet hover pour le bouton
        btnNew.setOnMouseEntered(e -> btnNew.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px; -fx-padding: 15 40; -fx-background-radius: 12; -fx-cursor: hand;"));
        btnNew.setOnMouseExited(e -> btnNew.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px; -fx-padding: 15 40; -fx-background-radius: 12; -fx-cursor: hand;"));

        btnNew.setOnAction(e -> { cartService.clear(); mainController.showHomeScreen(); });

        root.getChildren().addAll(icon, title, message, orderNumber, waitMessage, btnNew);
        mainController.getMainLayout().setCenter(root);
    }
}