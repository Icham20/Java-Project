package org.example.controller.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.controller.MainController;
import org.example.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DetailView {
    private final MainController controller;
    private final Product product;

    public DetailView(MainController controller, Product product) {
        this.controller = controller;
        this.product = product;
    }

    public Node getView() {
        BorderPane detailLayout = new BorderPane();
        detailLayout.setPadding(new Insets(20));

        // --- HAUT (Bouton Retour) ---
        HBox top = new HBox();
        Button backBtn = new Button(controller.getBundle().getString("detail.back"));
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> controller.showMenuScreen());
        top.getChildren().add(backBtn);
        detailLayout.setTop(top);

        // --- CENTRE (Contenu) ---
        HBox center = new HBox(60);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        // Image (Rectangle gris pour l'instant)
        Rectangle bigImg = new Rectangle(500, 400, Color.web("#f1f5f9"));
        bigImg.setArcWidth(30); bigImg.setArcHeight(30);

        // Colonne d'infos (Droite)
        VBox infoCol = new VBox(25);
        infoCol.setPrefWidth(500);

        Label name = new Label(product.getName());
        name.getStyleClass().add("title-large");
        name.setStyle("-fx-font-size: 48px;");

        Label desc = new Label(product.getDescription());
        desc.setStyle("-fx-font-size: 22px; -fx-text-fill: #cbd5e1;"); // Texte clair pour fond sombre
        desc.setWrapText(true);

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");
        price.setStyle("-fx-font-size: 32px;");

        // Bloc Options
        VBox optionsBox = new VBox(20);
        optionsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10,0,0,0);");

        Label lblOpt1 = new Label(controller.getBundle().getString("detail.spice"));
        lblOpt1.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1e293b;");
        ToggleGroup groupSpice = new ToggleGroup();
        RadioButton rb1 = new RadioButton(controller.getBundle().getString("detail.spice.mild")); rb1.setToggleGroup(groupSpice); rb1.setSelected(true);
        RadioButton rb2 = new RadioButton(controller.getBundle().getString("detail.spice.medium")); rb2.setToggleGroup(groupSpice);
        RadioButton rb3 = new RadioButton(controller.getBundle().getString("detail.spice.hot")); rb3.setToggleGroup(groupSpice);
        HBox boxSpice = new HBox(20, rb1, rb2, rb3);

        Label lblOpt2 = new Label(controller.getBundle().getString("detail.side"));
        lblOpt2.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1e293b;");
        ToggleGroup groupSide = new ToggleGroup();
        RadioButton rbRice = new RadioButton(controller.getBundle().getString("detail.side.rice")); rbRice.setToggleGroup(groupSide); rbRice.setSelected(true);
        RadioButton rbNoodle = new RadioButton(controller.getBundle().getString("detail.side.noodle")); rbNoodle.setToggleGroup(groupSide);
        HBox boxSide = new HBox(20, rbRice, rbNoodle);

        optionsBox.getChildren().addAll(lblOpt1, boxSpice, new Separator(), lblOpt2, boxSide);

        // Actions (Spinner + Bouton Ajouter)
        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER_LEFT);

        Spinner<Integer> spinner = new Spinner<>(1, 10, 1);
        spinner.setStyle("-fx-font-size: 20px; -fx-body-color: white;");
        spinner.setPrefHeight(50);
        spinner.setPrefWidth(100);

        Button btnAddCart = new Button(controller.getBundle().getString("detail.add"));
        btnAddCart.getStyleClass().add("btn-start");
        btnAddCart.setStyle("-fx-font-size: 22px; -fx-padding: 10 40;");

        btnAddCart.setOnAction(e -> {
            List<String> opts = new ArrayList<>();
            opts.add(((RadioButton)groupSpice.getSelectedToggle()).getText());
            opts.add(((RadioButton)groupSide.getSelectedToggle()).getText());
            controller.getCartService().addProduct(product, spinner.getValue(), opts);
            controller.showMenuScreen();
        });

        actions.getChildren().addAll(spinner, btnAddCart);

        infoCol.getChildren().addAll(name, desc, price, optionsBox, actions);
        center.getChildren().addAll(bigImg, infoCol);

        // === C'EST CETTE LIGNE QUI MANQUAIT ! ===
        detailLayout.setCenter(center);

        return detailLayout;
    }
}