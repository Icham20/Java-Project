package org.example.repository;

import org.example.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    /**
     * Récupère toutes les catégories de la base de données.
     * @return Une liste d'objets Category
     */
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}