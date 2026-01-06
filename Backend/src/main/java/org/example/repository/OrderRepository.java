package org.example.repository;

import org.example.model.Order;
import org.example.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    /**
     * Sauvegarde une commande et ses articles en base de données.
     * Utilise une transaction pour s'assurer que tout est sauvegardé ou rien du tout.
     * @param order La commande à sauvegarder (contenant la liste des items)
     * @return L'ID de la commande générée, ou -1 en cas d'erreur
     */
    public int save(Order order) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;
        int orderId = -1;

        String insertOrderSql = "INSERT INTO orders (customer_name, total_price) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO order_item (order_id, product_id, quantity, unit_price, options) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = DatabaseManager.getConnection();
            // 1. Désactiver l'auto-commit pour gérer la transaction manuellement
            conn.setAutoCommit(false);

            // 2. Insérer la commande principale
            orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, order.getCustomerName());
            orderStmt.setDouble(2, order.getTotalPrice());
            orderStmt.executeUpdate();

            // 3. Récupérer l'ID généré pour la commande
            generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setId(orderId); // Mettre à jour l'objet Java
            } else {
                throw new SQLException("Échec de la création de la commande, aucun ID obtenu.");
            }

            // 4. Insérer chaque article de la commande
            itemStmt = conn.prepareStatement(insertItemSql);
            for (OrderItem item : order.getItems()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setString(5, item.getOptions());
                itemStmt.addBatch(); // Ajout au batch pour performance
            }
            itemStmt.executeBatch(); // Exécuter toutes les insertions d'items d'un coup

            // 5. Valider la transaction
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Transaction annulée (Rollback)");
                    conn.rollback(); // Annuler tout si erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return -1;
        } finally {
            // 6. Fermer les ressources proprement
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (orderStmt != null) orderStmt.close();
                if (itemStmt != null) itemStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Remettre en état normal
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return orderId;
    }

    /**
     * Récupère une commande par son ID, avec tous ses items.
     */
    public Order findById(int id) {
        Order order = null;
        String sqlOrder = "SELECT * FROM orders WHERE id = ?";
        String sqlItems = "SELECT * FROM order_item WHERE order_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {

            // 1. Récupérer la commande
            stmtOrder.setInt(1, id);
            try (ResultSet rsOrder = stmtOrder.executeQuery()) {
                if (rsOrder.next()) {
                    order = new Order();
                    order.setId(rsOrder.getInt("id"));
                    order.setCustomerName(rsOrder.getString("customer_name"));
                    order.setTotalPrice(rsOrder.getDouble("total_price"));
                    order.setCreatedAt(rsOrder.getString("created_at"));
                }
            }

            // Si la commande existe, on récupère ses items
            if (order != null) {
                stmtItems.setInt(1, id);
                try (ResultSet rsItems = stmtItems.executeQuery()) {
                    List<OrderItem> items = new ArrayList<>();
                    while (rsItems.next()) {
                        OrderItem item = new OrderItem();
                        item.setId(rsItems.getInt("id"));
                        item.setOrderId(rsItems.getInt("order_id"));
                        item.setProductId(rsItems.getInt("product_id"));
                        item.setQuantity(rsItems.getInt("quantity"));
                        item.setUnitPrice(rsItems.getDouble("unit_price"));
                        item.setOptions(rsItems.getString("options"));
                        items.add(item);
                    }
                    order.setItems(items);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }
}