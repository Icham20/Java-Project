package org.example;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        }).start(7000);

        app.get("/", ctx -> ctx.result("Backend Restaurant API is running"));

        // TODO: Enregistrer les routes ici
        // ex: app.get("/products", ctx -> productController.getAll(ctx));
    }
}
