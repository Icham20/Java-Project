package org.example.utils;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Boîte à outils pour l'interface graphique (Images, Alertes, Notifications).
 */
public class InterfaceTools {

    /** Crée un bouton de langue stylisé. */
    public static Button createLangBtn(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-lang");
        b.setPrefWidth(80);
        b.setPrefHeight(40);
        return b;
    }

    /** Charge une image et l'arrondit (ou affiche un carré gris si absente). */
    public static Node createProductImageNode(String imageUrl, double width, double height) {
        String imagePath = "/org/example/images/" + imageUrl;
        if (imageUrl != null && !imageUrl.isEmpty() && InterfaceTools.class.getResource(imagePath) != null) {
            ImageView imageView = new ImageView(new Image(InterfaceTools.class.getResourceAsStream(imagePath)));
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);

            Rectangle clip = new Rectangle(width, height);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);
            return imageView;
        } else {
            Rectangle imgPlace = new Rectangle(width, height, Color.web("#f1f5f9"));
            imgPlace.setArcWidth(15);
            imgPlace.setArcHeight(15);
            return imgPlace;
        }
    }

    /** Affiche une petite notification verte temporaire (Toast). */
    public static void showQuickNotification(String message) {
        Stage notifStage = new Stage();
        notifStage.initStyle(StageStyle.UNDECORATED);
        notifStage.initModality(Modality.NONE);

        VBox notifBox = new VBox(10);
        notifBox.setAlignment(Pos.CENTER);
        notifBox.setPadding(new javafx.geometry.Insets(20));
        notifBox.setStyle("-fx-background-color: #d1fae5; -fx-background-radius: 10; -fx-border-color: #10b981; -fx-border-width: 2; -fx-border-radius: 10;");

        Label notifLabel = new Label(message);
        notifLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #065f46;");

        notifBox.getChildren().add(notifLabel);
        Scene notifScene = new Scene(notifBox);
        notifStage.setScene(notifScene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        notifStage.setX(screenBounds.getWidth() - 350);
        notifStage.setY(screenBounds.getHeight() - 150);

        notifStage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> notifStage.close());
        delay.play();
    }

    /** Affiche une alerte bloquante pour les erreurs ou informations importantes. */
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}