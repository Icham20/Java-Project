package org.example.repository;

import org.example.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    /**
     * Récupère TOUS les produits de la base de données.
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Récupère les produits d'une catégorie spécifique.
     * @param categoryId l'ID de la catégorie (ex: 1 pour Entrées)
     */
    public List<Product> findByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE category_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // On remplace le premier '?' par la valeur de categoryId
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Récupère un produit spécifique par son ID.
     * @param id l'ID du produit
     * @return Le produit trouvé ou null si inexistant
     */
    public Product findById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Met à jour le stock d'un produit.
     * @param productId ID du produit
     * @param quantityToRemove Quantité à retirer
     * @return true si succès
     */
    public boolean updateStock(int productId, int quantityToRemove) {
        String sql = "UPDATE product SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantityToRemove);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantityToRemove); // Vérifie qu'il y a assez de stock
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Méthode utilitaire pour transformer une ligne SQL en objet Java Product.
     * Évite de répéter le même code dans chaque méthode.
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("category_id"),
            rs.getString("image_url"),
            rs.getBoolean("is_spicy"),
            rs.getBoolean("available"),
            rs.getInt("stock_quantity") // Récupération du champ non exposé
        );
    }
}
