# Restaurant Frontend Kiosk

Ceci est l'interface utilisateur (JavaFX) de la borne de commande de restauration asiatique.

## Technologies utilisées
- Java 17
- JavaFX (UI)
- Jackson (JSON processing)
- Maven (Build tool)

## Lancement de l'application
1. Assurez-vous que le backend est lancé sur `http://localhost:7000`.
2. Compilez le projet : `mvn clean install`
3. Lancez l'application : `mvn javafx:run`

## Fonctionnalités
- Écran d'accueil
- Sélection de catégories
- Liste de plats par catégorie
- Détail du plat avec options de personnalisation
- Gestion du panier (quantité, suppression)
- Confirmation de commande avec numéro unique
