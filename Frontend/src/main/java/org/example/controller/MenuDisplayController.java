package org.example.controller;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.model.Product;
import org.example.services.CartService;
import org.example.utils.InterfaceTools;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur gérant l'affichage visuel des produits (Grille Menu et Page Détail).
 */
public class MenuDisplayController {

    private final CartService cartService;
    private final MainAppController mainController;
    private final SmartSuggestionPopup suggestionPopup;

    public MenuDisplayController(CartService cartService, MainAppController mainController, SmartSuggestionPopup suggestionPopup) {
        this.cartService = cartService;
        this.mainController = mainController;
        this.suggestionPopup = suggestionPopup;
    }

    /** Crée une carte produit pour la grille du menu. */
    public HBox createProductCard(Product product, ResourceBundle bundle) {
        HBox card = new HBox(20);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(550);
        card.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().add(InterfaceTools.createProductImageNode(product.getImageUrl(), 140, 140));

        VBox info = new VBox(10);
        HBox.setHgrow(info, Priority.ALWAYS);
        info.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(mainController.getTranslateName(product));
        name.getStyleClass().add("h2");
        name.setStyle("-fx-font-size: 24px;");

        Label desc = new Label(mainController.getTranslateDesc(product));
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #64748b;");

        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button(bundle.getString("menu.add"));
        btnAdd.getStyleClass().add("btn-add-product");

        if (!product.isAvailable()) {
            btnAdd.setText("N/A");
            btnAdd.setDisable(true);
            btnAdd.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-opacity: 1;");
        } else {
            btnAdd.setOnAction(e -> showDetailScreen(product, bundle));
        }
        bottomRow.getChildren().addAll(price, spacer, btnAdd);

        info.getChildren().addAll(name, desc, bottomRow);
        card.getChildren().addAll(info);
        return card;
    }

    /** Affiche la page de détail d'un produit avec les options conditionnelles. */
    public void showDetailScreen(Product product, ResourceBundle bundle) {
        BorderPane detailLayout = new BorderPane();
        detailLayout.setPadding(new Insets(20));
        HBox top = new HBox();
        Button backBtn = new Button(bundle.getString("detail.back"));
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> mainController.showMenuScreen());
        top.getChildren().add(backBtn);
        detailLayout.setTop(top);

        HBox center = new HBox(60);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        center.getChildren().add(InterfaceTools.createProductImageNode(product.getImageUrl(), 500, 400));

        VBox infoCol = new VBox(25);
        infoCol.setPrefWidth(500);
        Label name = new Label(mainController.getTranslateName(product));
        name.getStyleClass().add("title-large");
        name.setStyle("-fx-font-size: 48px;");
        Label desc = new Label(mainController.getTranslateDesc(product));
        desc.setStyle("-fx-font-size: 22px; -fx-text-fill: #64748b;");
        desc.setWrapText(true);

        Label price = new Label(String.format("%.2f €", product.getPrice()));
        price.getStyleClass().add("price-text");
        price.setStyle("-fx-font-size: 32px;");

        VBox optionsBox = new VBox(20);
        optionsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,0);");

        java.util.function.Supplier<List<String>> optionsSupplier;
        long catId = product.getCategoryId();

        // GESTION DES OPTIONS PAR CATEGORIE (Plats(=2), dessert (=3), boisson(=4) )
        if (catId == 2) {
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
            optionsSupplier = () -> {
                List<String> opts = new ArrayList<>();
                if(groupSpice.getSelectedToggle() != null) opts.add(((RadioButton) groupSpice.getSelectedToggle()).getText());
                if(groupSide.getSelectedToggle() != null) opts.add(((RadioButton) groupSide.getSelectedToggle()).getText());
                return opts;
            };
        } else if (catId == 3) {
            Label lblCoffee = new Label(bundle.getString("detail.coffee"));
            lblCoffee.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            ToggleGroup groupCoffee = new ToggleGroup();
            RadioButton rbYes = new RadioButton(bundle.getString("detail.yes")); rbYes.setToggleGroup(groupCoffee);
            RadioButton rbNo = new RadioButton(bundle.getString("detail.no")); rbNo.setToggleGroup(groupCoffee); rbNo.setSelected(true);
            HBox boxCoffee = new HBox(20, rbYes, rbNo);
            optionsBox.getChildren().addAll(lblCoffee, boxCoffee);
            optionsSupplier = () -> { List<String> opts = new ArrayList<>(); if (rbYes.isSelected()) opts.add(bundle.getString("option.with_coffee")); return opts; };
        } else if (catId == 4) {
            Label lblIce = new Label(bundle.getString("detail.ice"));
            lblIce.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            ToggleGroup groupIce = new ToggleGroup();
            RadioButton rbIceYes = new RadioButton(bundle.getString("detail.yes")); rbIceYes.setToggleGroup(groupIce); rbIceYes.setSelected(true);
            RadioButton rbIceNo = new RadioButton(bundle.getString("detail.no")); rbIceNo.setToggleGroup(groupIce);
            HBox boxIce = new HBox(20, rbIceYes, rbIceNo);
            optionsBox.getChildren().addAll(lblIce, boxIce);
            optionsSupplier = () -> { List<String> opts = new ArrayList<>(); if (rbIceYes.isSelected()) opts.add(bundle.getString("option.with_ice")); else opts.add(bundle.getString("option.no_ice")); return opts; };
        } else {
            // Entrées (Cat 1) : Pas d'options
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

            suggestionPopup.tryShowSuggestion(
                    product,
                    (Stage) mainController.getMainLayout().getScene().getWindow(),
                    bundle,
                    mainController
            );

            PauseTransition delay = new PauseTransition(Duration.millis(300));
            delay.setOnFinished(event -> mainController.showMenuScreen());
            delay.play();
        });

        actions.getChildren().addAll(spinner, btnAddCart);

        infoCol.getChildren().addAll(name, desc);
        infoCol.getChildren().addAll(price, optionsBox, actions);
        center.getChildren().addAll(infoCol);
        detailLayout.setCenter(center);
        mainController.getMainLayout().setCenter(detailLayout);
    }
}