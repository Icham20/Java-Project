package org.example.controller.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.controller.MainController;
import org.example.model.CartItem;

public class CartView {
    private final MainController controller;

    public CartView(MainController controller) {
        this.controller = controller;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(50, 100, 50, 100)); // Marges ajustées
        root.setAlignment(Pos.TOP_CENTER);

        // Boite blanche du ticket
        VBox receipt = new VBox(20);
        receipt.getStyleClass().add("receipt-box");

        // Titre
        Label title = new Label(controller.getBundle().getString("cart.title"));
        title.getStyleClass().add("h1"); // Style titre foncé
        title.setAlignment(Pos.CENTER);

        // --- TABLEAU 5 COLONNES ---
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Définition des largeurs de colonnes (Important pour l'alignement)
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(25); // Nom
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(30); // Options
        ColumnConstraints col3 = new ColumnConstraints(); col3.setPercentWidth(10); // Qté
        ColumnConstraints col4 = new ColumnConstraints(); col4.setPercentWidth(15); // Prix
        ColumnConstraints col5 = new ColumnConstraints(); col5.setPercentWidth(20); // Actions
        grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5);

        // En-têtes
        addHeader(grid, controller.getBundle().getString("cart.article"), 0);
        addHeader(grid, "Options", 1); // Nouvelle colonne
        addHeader(grid, controller.getBundle().getString("cart.qty"), 2);
        addHeader(grid, controller.getBundle().getString("cart.total"), 3);
        addHeader(grid, "Actions", 4);

        int row = 1;
        if (controller.getCartService().getItems().isEmpty()) {
            Label emptyMsg = new Label("Votre panier est vide.");
            emptyMsg.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-font-style: italic;");
            grid.add(emptyMsg, 0, 1, 5, 1);
        } else {
            for (CartItem item : controller.getCartService().getItems()) {

                // Col 1 : Nom du produit
                Label name = new Label(item.getProduct().getName());
                name.getStyleClass().add("text-dark"); // Force la couleur foncée
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
                name.setWrapText(true);

                // Col 2 : Options (Accompagnement, Épice...)
                Label opts = new Label(String.join("\n", item.getOptions())); // \n pour saut de ligne
                opts.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-style: italic;");
                opts.setWrapText(true);

                // Col 3 : Quantité
                Label qty = new Label("x" + item.getQuantity());
                qty.getStyleClass().add("text-dark");
                qty.setStyle("-fx-font-size: 18px; -fx-alignment: CENTER;");

                // Col 4 : Prix Total Ligne
                Label price = new Label(String.format("%.2f €", item.getTotalPrice()));
                price.getStyleClass().add("text-dark");
                price.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

                // Col 5 : Actions
                HBox actionsBox = new HBox(10);
                actionsBox.setAlignment(Pos.CENTER_LEFT);

                Button btnEdit = new Button("✎");
                btnEdit.getStyleClass().add("btn-edit");
                btnEdit.setOnAction(e -> {
                    controller.getCartService().removeProduct(item);
                    controller.showDetailScreen(item.getProduct());
                });

                Button btnDel = new Button("🗑");
                btnDel.getStyleClass().add("btn-trash");
                btnDel.setOnAction(e -> {
                    controller.getCartService().removeProduct(item);
                    controller.showCartScreen();
                });

                actionsBox.getChildren().addAll(btnEdit, btnDel);

                // Ajout à la grille
                grid.add(name, 0, row);
                grid.add(opts, 1, row);
                grid.add(qty, 2, row);
                grid.add(price, 3, row);
                grid.add(actionsBox, 4, row);
                row++;
            }
        }

        Separator sep = new Separator();

        // Total Global
        HBox totalBox = new HBox(20);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        Label lblTotal = new Label(controller.getBundle().getString("cart.total_pay"));
        lblTotal.getStyleClass().add("text-dark");
        lblTotal.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Un peu plus gros pour le label

        Label valTotal = new Label(String.format("%.2f €", controller.getCartService().getTotal()));
        // On utilise "text-dark" pour avoir la couleur gris foncé (comme 6,90 €)
        valTotal.getStyleClass().add("text-dark");
        // On garde une grande taille, mais sans l'ombre du "title-large"
        valTotal.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        totalBox.getChildren().addAll(lblTotal, valTotal);

        // Champs Client
        VBox clientBox = new VBox(10);
        Label lblClient = new Label(controller.getBundle().getString("cart.client_placeholder"));
        lblClient.setStyle("-fx-text-fill: #64748b;");
        TextField txtClient = new TextField();
        txtClient.setStyle("-fx-font-size: 18px; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #cbd5e1;");
        clientBox.getChildren().addAll(lblClient, txtClient);

        receipt.getChildren().addAll(title, new Separator(), grid, sep, totalBox, clientBox);

        // Footer Actions
        HBox actions = new HBox(40);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(30,0,0,0));

        Button btnBack = new Button(controller.getBundle().getString("cart.modify"));
        btnBack.getStyleClass().add("btn-secondary");
        btnBack.setText("← Continuer achats");
        btnBack.setOnAction(e -> controller.showMenuScreen());

        Button btnPay = new Button(controller.getBundle().getString("cart.validate"));
        btnPay.getStyleClass().add("btn-start");
        btnPay.setOnAction(e -> controller.showConfirmationScreen(1234));

        actions.getChildren().addAll(btnBack, btnPay);

        root.getChildren().addAll(receipt, actions);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    // Petite méthode utilitaire pour les en-têtes
    private void addHeader(GridPane grid, String text, int col) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold; -fx-text-fill: #94a3b8; -fx-font-size: 16px;");
        grid.add(l, col, 0);
    }
}