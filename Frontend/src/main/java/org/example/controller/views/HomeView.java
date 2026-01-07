package org.example.controller.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.MainController;

public class HomeView {
    private final MainController controller;

    public HomeView(MainController controller) {
        this.controller = controller;
    }

    public Node getView() {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);

        Label logo = new Label(controller.getBundle().getString("app.title"));
        logo.getStyleClass().add("title-large");

        Button startBtn = new Button(controller.getBundle().getString("home.cta"));
        startBtn.getStyleClass().add("btn-start");
        startBtn.setOnAction(e -> controller.showMenuScreen());

        Label subTitle = new Label(controller.getBundle().getString("home.subtitle"));
        subTitle.getStyleClass().add("subtitle");

        HBox langBox = new HBox(20);
        langBox.setAlignment(Pos.CENTER);

        Button btnFR = createLangBtn("FR");
        btnFR.setOnAction(e -> controller.loadLanguage("fr"));

        Button btnEN = createLangBtn("EN");
        btnEN.setOnAction(e -> controller.loadLanguage("en"));

        Button btnHelp = createLangBtn(controller.getBundle().getString("home.help"));

        langBox.getChildren().addAll(btnFR, btnEN, btnHelp);
        root.getChildren().addAll(logo, subTitle, startBtn);

        BorderPane pane = new BorderPane();
        pane.setCenter(root);
        pane.setBottom(langBox);
        BorderPane.setMargin(langBox, new Insets(0,0,40,0));
        return pane;
    }

    private Button createLangBtn(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-lang");
        b.setPrefWidth(80);
        b.setPrefHeight(40);
        return b;
    }
}
