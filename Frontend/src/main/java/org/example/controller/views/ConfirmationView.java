package org.example.controller.views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.controller.MainController;

public class ConfirmationView {
    private final MainController controller;
    private final int orderId;

    public ConfirmationView(MainController controller, int orderId) {
        this.controller = controller;
        this.orderId = orderId;
    }

    public Node getView() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 100px;");

        Label title = new Label(controller.getBundle().getString("confirm.title"));
        title.getStyleClass().add("title-large");

        Label msg = new Label(controller.getBundle().getString("confirm.msg"));
        msg.getStyleClass().add("subtitle");

        Label numCmd = new Label("#" + orderId);
        numCmd.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-text-fill: #d97706;");

        Label waitMsg = new Label(controller.getBundle().getString("confirm.wait"));
        waitMsg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20px;");

        Button btnNew = new Button(controller.getBundle().getString("confirm.new"));
        btnNew.getStyleClass().add("btn-secondary");
        btnNew.setOnAction(e -> {
            controller.getCartService().clear();
            controller.showHomeScreen();
        });

        root.getChildren().addAll(icon, title, msg, numCmd, waitMsg, btnNew);
        return root;
    }
}