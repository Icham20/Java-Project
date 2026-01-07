package org.example.model;

public class Product {
    private Long id;
    private String name;
    private double price;
    private String description;
    private Long categoryId;

    // Champs supplémentaires venant du DTO backend
    private String imageUrl;
    private boolean spicy;
    private boolean vegetarian;
    private boolean available;

    public Product() {}

    public Product(Long id, String name, double price, String description, Long categoryId,
                   String imageUrl, boolean spicy, boolean vegetarian, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.spicy = spicy;
        this.vegetarian = vegetarian;
        this.available = available;
    }

    // Getters & Setters pour tous les champs
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isSpicy() { return spicy; }
    public void setSpicy(boolean spicy) { this.spicy = spicy; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
