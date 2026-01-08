package org.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.example.model.Category;
import org.example.model.Product;
import org.example.services.ApiService;
import org.example.services.CartService;
import org.example.utils.InterfaceTools;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contrôleur Principal (Point d'entrée).
 * Initialise l'application, gère la navigation et délègue aux sous-contrôleurs.
 */
public class MainAppController {

    @FXML private BorderPane mainLayout;

    private final ApiService apiService = new ApiService();
    private final CartService cartService = CartService.getInstance();

    // Sous-Contrôleurs délégués
    private final SmartSuggestionPopup suggestionPopup;
    private final MenuDisplayController menuDisplayController;
    private final ShoppingCartController shoppingCartController;

    private Category currentCategory;
    private ResourceBundle bundle;
    private Locale currentLocale = new Locale("fr");

    public MainAppController() {
        // Initialisation des contrôleurs spécialisés (Separation of Concerns)
        this.suggestionPopup = new SmartSuggestionPopup(apiService, cartService);
        this.menuDisplayController = new MenuDisplayController(cartService, this, suggestionPopup);
        this.shoppingCartController = new ShoppingCartController(cartService, apiService, this);
    }

    /** Méthode d'initialisation JavaFX. */
    @FXML
    public void initialize() {
        if(mainLayout != null) {
            mainLayout.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());
            loadLanguage("fr");
        }
    }

    public BorderPane getMainLayout() {
        return mainLayout;
    }

    /** Charge la langue (FR/EN) et met à jour l'interface. */
    public void loadLanguage(String lang) {
        currentLocale = new Locale(lang);
        try {
            this.bundle = ResourceBundle.getBundle("org.example.strings", currentLocale, new UTF8Control());
        } catch (Exception e) {
            this.bundle = ResourceBundle.getBundle("org.example.strings", currentLocale);
        }
        showHomeScreen();
    }

    // --- Helpers de Traduction (accessibles par les autres contrôleurs) ---
    public String getTranslateName(Product p) {
        String key = "prod." + p.getId() + ".name";
        if (bundle.containsKey(key)) return bundle.getString(key);
        return p.getName();
    }

    public String getTranslateDesc(Product p) {
        String key = "prod." + p.getId() + ".desc";
        if (bundle.containsKey(key)) return bundle.getString(key);
        return p.getDescription();
    }

    public String getTranslateCat(Category c) {
        String key = "cat." + c.getId();
        if (bundle.containsKey(key)) return bundle.getString(key);
        return c.getName();
    }

    /** Gestionnaire d'encodage UTF-8 pour les fichiers .properties */
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

    // --- 1. ACCUEIL ---
    /** Affiche l'écran d'accueil avec choix de langue. */
    public void showHomeScreen() {
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
        Button btnFR = InterfaceTools.createLangBtn("FR");
        btnFR.setOnAction(e -> loadLanguage("fr"));
        Button btnEN = InterfaceTools.createLangBtn("EN");
        btnEN.setOnAction(e -> loadLanguage("en"));

        langBox.getChildren().addAll(btnFR, btnEN);
        root.getChildren().addAll(logo, subTitle, startBtn);
        BorderPane pane = new BorderPane(); pane.setCenter(root); pane.setBottom(langBox); BorderPane.setMargin(langBox, new Insets(0,0,40,0));
        mainLayout.setCenter(pane);
    }

    // --- 2. MENU ---
    /** Affiche l'écran principal du menu (catégories et produits). */
    public void showMenuScreen() {
        BorderPane menuLayout = new BorderPane(); menuLayout.setPadding(new Insets(20, 40, 20, 40));
        HBox header = new HBox(30); header.setAlignment(Pos.CENTER_LEFT); header.setPadding(new Insets(0, 0, 20, 0));
        Label lblMenu = new Label(bundle.getString("menu.title")); lblMenu.getStyleClass().add("menu-title-orange");
        HBox tabs = new HBox(10); tabs.setAlignment(Pos.CENTER); HBox.setHgrow(tabs, Priority.ALWAYS);

        List<Category> categories = apiService.getCategories();
        if (categories.isEmpty()) { showErrorScreen("Error / Erreur connexion"); return; }
        if (currentCategory == null) currentCategory = categories.get(0);

        for (Category cat : categories) {
            Button tab = new Button(getTranslateCat(cat));
            tab.getStyleClass().add("tab-button");
            if (currentCategory.getId().equals(cat.getId())) tab.getStyleClass().add("tab-active");
            tab.setOnAction(e -> { currentCategory = cat; showMenuScreen(); });
            tabs.getChildren().add(tab);
        }

        Button btnCartTop = new Button("🛒 " + String.format("%.2f €", cartService.getTotal()));
        btnCartTop.getStyleClass().add("btn-primary");
        btnCartTop.setOnAction(e -> showCartScreen());
        header.getChildren().addAll(lblMenu, tabs, btnCartTop);
        menuLayout.setTop(header);

        List<Product> products = apiService.getProductsByCategory(currentCategory.getId());
        if (products.isEmpty()) {
            VBox noProductsBox = new VBox(20); noProductsBox.setAlignment(Pos.CENTER); noProductsBox.setPadding(new Insets(50));
            Label noProductsLabel = new Label("Aucun produit."); noProductsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
            noProductsBox.getChildren().add(noProductsLabel); menuLayout.setCenter(noProductsBox);
        } else {
            TilePane grid = new TilePane(); grid.setHgap(30); grid.setVgap(30); grid.setPrefColumns(2); grid.setAlignment(Pos.TOP_CENTER); grid.setPadding(new Insets(20));
            for (Product product : products) {
                // Délégation de la création graphique au MenuDisplayController
                grid.getChildren().add(menuDisplayController.createProductCard(product, bundle));
            }
            ScrollPane scroll = new ScrollPane(grid); scroll.setFitToWidth(true); scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            menuLayout.setCenter(scroll);
        }

        HBox footer = new HBox(20); footer.setPadding(new Insets(20, 0, 0, 0)); footer.setAlignment(Pos.CENTER_LEFT);
        Button btnAccueil = new Button(bundle.getString("menu.back")); btnAccueil.getStyleClass().add("btn-secondary"); btnAccueil.setOnAction(e -> showHomeScreen());
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnPanier = new Button(bundle.getString("menu.cart")); btnPanier.getStyleClass().add("btn-start"); btnPanier.setStyle("-fx-font-size: 22px; -fx-padding: 10 30;"); btnPanier.setOnAction(e -> showCartScreen());
        footer.getChildren().addAll(btnAccueil, spacer, btnPanier);
        menuLayout.setBottom(footer);
        mainLayout.setCenter(menuLayout);
    }

    /** Affiche l'écran du panier via le CartController. */
    private void showCartScreen() {
        shoppingCartController.showCartScreen(mainLayout, bundle);
    }

    /** Affiche un écran d'erreur en cas de problème API. */
    private void showErrorScreen(String errorMessage) {
        VBox errorBox = new VBox(30); errorBox.setAlignment(Pos.CENTER); errorBox.setPadding(new Insets(50));
        Label errorIcon = new Label("⚠️"); errorIcon.setStyle("-fx-font-size: 60px;");
        Label errorLabel = new Label("Erreur de connexion"); errorLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        Label errorDetail = new Label(errorMessage); errorDetail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");
        Button retryBtn = new Button("Réessayer"); retryBtn.getStyleClass().add("btn-primary"); retryBtn.setOnAction(e -> showMenuScreen());
        Button backBtn = new Button("Retour à l'accueil"); backBtn.getStyleClass().add("btn-secondary"); backBtn.setOnAction(e -> showHomeScreen());
        errorBox.getChildren().addAll(errorIcon, errorLabel, errorDetail, retryBtn, backBtn);
        mainLayout.setCenter(errorBox);
    }
}