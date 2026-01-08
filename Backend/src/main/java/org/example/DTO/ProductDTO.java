package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO pour transférer les informations produit au frontend.
 * Contient SEULEMENT les données nécessaires à l'affichage.
 */
public class ProductDTO {

    // ==================== CHAMPS ====================
    // Ce sont les données que le FRONTEND a besoin

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private double price;

    @JsonProperty("categoryId")
    private Long categoryId;

    // === CHAMPS POUR LES FONCTIONNALITÉS DU PROJET ===

    @JsonProperty("imageUrl")
    private String imageUrl;           // Pour afficher l'image du plat

    @JsonProperty("spicy")
    private boolean spicy;             // Pour l'icône 🌶️ (épicé)

    @JsonProperty("available")
    private boolean available;         // Pour griser si non disponible

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur vide OBLIGATOIRE pour Jackson.
     */
    public ProductDTO() {
    }

    /**
     * Constructeur de conversion depuis un Product (modèle BDD).
     * DÉMONSTRATION DTO : On utilise le stockQuantity du modèle interne
     * pour calculer 'available', mais on ne l'expose pas dans le JSON final.
     */
    public ProductDTO(org.example.model.Product product) {
        this.id = (long) product.getId();
        this.categoryId = (long) product.getCategoryId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.spicy = product.isSpicy();
        
        // Logique interne : stock <= 0 signifie que le produit devient indisponible
        this.available = product.isAvailable() && (product.getStockQuantity() > 0);
    }

    // ==================== GETTERS & SETTERS ====================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSpicy() {
        return spicy;
    }

    public void setSpicy(boolean spicy) {
        this.spicy = spicy;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // ==================== MÉTHODES UTILES ====================

    /**
     * Méthode pour faciliter le debug.
     * Affiche les informations principales.
     */
    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }

    /**
     * Méthode utilitaire pour le frontend.
     * @return L'icône épicé si applicable
     */
    public String getSpicyIcon() {
        return spicy ? "🌶️" : "";
    }

    /**
     * Méthode utilitaire pour le frontend.
     * @return Le prix formaté (ex: "5.50€")
     */
    public String getFormattedPrice() {
        return String.format("%.2f€", price);
    }
}