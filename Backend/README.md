# Restaurant Backend API

Ceci est le backend du projet de borne de commande de restauration asiatique.

## Technologies utilisées
- Java 17
- Javalin (Framework Web)
- Jackson (JSON processing)
- Maven (Build tool)

## Lancement du serveur
1. Assurez-vous d'avoir Maven installé.
2. Compilez le projet : `mvn clean install`
3. Lancez le serveur : `mvn exec:java -Dexec.mainClass="org.example.Main"`

Le serveur sera accessible sur `http://localhost:7000`.

## Endpoints
- `GET /categories` : Liste toutes les catégories.
- `GET /products` : Liste tous les produits (peut être filtré par `?category=ID`).
- `POST /orders` : Crée une nouvelle commande.
- `GET /orders/{id}` : Récupère les détails d'une commande.
