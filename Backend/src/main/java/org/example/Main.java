package org.example;

import io.javalin.Javalin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // 1. On démarre le serveur Web (Javalin)
        Javalin app = Javalin.create().start(7000);

        // 2. On définit la route "/users" que le prof demande
        app.get("/users", ctx -> {

            // --- Connexion à la base de données ---
            String url = "jdbc:mysql://localhost:3306/projet_db";
            String user = "user_projet";
            String password = "password123";

            ArrayList<Map<String, String>> utilisateurs = new ArrayList<>();

            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM User");

                // On transforme chaque ligne de la BDD en objet pour le site web
                while (resultSet.next()) {
                    Map<String, String> unUser = new HashMap<>();
                    unUser.put("nom", resultSet.getString("username"));
                    unUser.put("email", resultSet.getString("email"));
                    utilisateurs.add(unUser);
                }
                connection.close();
            } catch (Exception e) {
                e.printStackTrace(); // Affiche l'erreur dans la console si besoin
            }

            // 3. On envoie la liste au navigateur (JSON)
            ctx.json(utilisateurs);
        });

        System.out.println("✅ Serveur prêt ! Ouvre http://localhost:7000/users");
    }
}