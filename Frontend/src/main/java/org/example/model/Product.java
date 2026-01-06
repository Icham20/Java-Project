package org.example.model;

public class Product {
    private Long id;
    private String name;
    private double price;
    private String description;
    private Long categoryId;

    public Product() {}
    public Product(Long id, String name, double price, String description, Long categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
}