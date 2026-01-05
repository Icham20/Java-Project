package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class MainController {
    @FXML private Label statusLabel;

    @FXML
    protected void onTestConnection() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:7000/ping")).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(res -> javafx.application.Platform.runLater(() -> statusLabel.setText("Réponse : " + res)))
            .exceptionally(ex -> {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Erreur : Serveur éteint ?"));
                return null;
            });
    }
}