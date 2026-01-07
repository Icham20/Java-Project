package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO pour les catégories.
 */
public class CategoryDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    // Constructeur vide obligatoire
    public CategoryDTO() {}

    // Constructeur depuis modèle
    public CategoryDTO(org.example.model.Category category) {
        this.id = (long) category.getId();
        this.name = category.getName();
    }

    // Getters & Setters
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
}