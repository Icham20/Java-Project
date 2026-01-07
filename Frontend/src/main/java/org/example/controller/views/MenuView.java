package org.example.controller.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.controller.MainController;
import org.example.model.Category;
import org.example.model.Product;
// L'import de MockDatabase a été retiré ici

import java.util.List;

public class MenuView {
    private final MainController controller;
    private static Category currentCategory;

    public MenuView(MainController controller) {
        this.controller = controller;
    }

    public Node getView() {
        BorderPane menuLayout = new BorderPane();
        menuLayout.setPadding(new Insets(20, 40, 20, 40));

        // Header
        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label lblMenu = new Label(controller.getBundle().getString("menu.title"));
        lblMenu.getStyleClass().add("menu-title-orange");

        HBox tabs = new HBox(10);
        tabs.setAlignment(Pos.CENTER);
        HBox.setHgrow(tabs, Priority.ALWAYS);

        // 1. On récupère les VRAIES catégories depuis l'API
        List<Category> categories = controller.getApiService().getCategories();

        // (J'ai supprimé la ligne qui appelait MockDatabase ici)

        if(currentCategory == null && !categories.isEmpty()) currentCategory = categories.get(0);

        for (Category cat : categories) {
            Button tab = new Button(cat.getName());
            tab.getStyleClass().add("tab-button");
            if (currentCategory != null && currentCategory.getId().equals(cat.getId())) {
                tab.getStyleClass().add("tab-active");
            }
            tab.setOnAction(e -> {
                currentCategory = cat;
                controller.showMenuScreen();
            });
            tabs.getChildren().add(tab);
        }

        Button btnCartTop = new Button("🛒 " + String.format("%.2f €", controller.getCartService().getTotal()));
        btnCartTop.getStyleClass().add("btn-primary");
        btnCartTop.setOnAction(e -> controller.showCartScreen());

        header.getChildren().addAll(lblMenu, tabs, btnCartTop);
        menuLayout.setTop(header);

        // Grille Produits
        TilePane grid = new TilePane();
        grid.setHgap(30); grid.setVgap(30);
        grid.setPrefColumns(2);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(20));

        // 2. On récupère les VRAIS produits depuis l'API
        if (currentCategory != null) {
            List<Product> products = controller.getApiService().getProductsByCategory(currentCategory.getId());

            // (J'ai supprimé la ligne qui appelait MockDatabase ici aussi)

            for (Product p : products) {
                grid.getChildren().add(createProductCard(p));
            }
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        menuLayout.setCenter(scroll);

        // Footer
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(20, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);

        Button btnAccueil = new Button(controller.getBundle().getString("menu.back"));
        btnAccueil.getStyleClass().add("btn-secondary");
        btnAccueil.setOnAction(e -> controller.showHomeScreen());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPanier = new Button(controller.getBundle().getString("menu.cart"));
        btnPanier.getStyleClass().add("btn-start");
        btnPanier.setStyle("-fx-font-size: 22px; -fx-padding: 10 30;");
        btnPanier.setOnAction(e -> controller.showCartScreen());

        footer.getChildren().addAll(btnAccueil, spacer, btnPanier);
        menuLayout.setBottom(footer);

        return menuLayout;
    }

    private HBox createProductCard(Product p) {
        HBox card = new HBox(20);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(550);
        card.setAlignment(Pos.CENTER_LEFT);

        // Essai de chargement d'image (Placeholder gris)
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

        Button btnAdd = new Button(controller.getBundle().getString("menu.add"));
        btnAdd.getStyleClass().add("btn-add-product");
        btnAdd.setOnAction(e -> controller.showDetailScreen(p));

        bottomRow.getChildren().addAll(price, r, btnAdd);
        info.getChildren().addAll(name, desc, bottomRow);

        card.getChildren().addAll(imgPlace, info);
        return card;
    }
}