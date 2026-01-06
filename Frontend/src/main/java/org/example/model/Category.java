package org.example.model;

public class Category {
    private Long id;
    private String name;

    public Category() {}
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
}