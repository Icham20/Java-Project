# 🍜 Borne de Commande - Resto'Asiat

Ce projet a été réalisé par l'équipe composée de :
*   **Elouan QUENTEL**
*   **Ouday GDIRI**
*   **Icham OULALI**
*   **Pierre Michel NDENGUE BOUNOUNOU**

---

Ce projet implémente une borne de commande interactive pour un restaurant asiatique. Il est composé d'une **API Backend** (Javalin/MySQL) et d'un **Client Frontend** (JavaFX).

Voici la procédure pas-à-pas pour lancer le projet complet.

## 📋 Prérequis

Assurez-vous d'avoir installé sur votre machine :
*   **Docker Desktop** (et qu'il est lancé).
*   **Java JDK 17** (ou version supérieure).
*   **Maven** (installé et accessible dans le terminal).

---

## 🚀 Guide de Lancement

### Étape 1 : Démarrer le Serveur et la Base de Données

Nous utilisons Docker pour configurer automatiquement la base de données MySQL et lancer l'API Backend.

1.  Ouvrez un terminal (PowerShell, CMD ou Bash).
2.  Placez-vous à la racine du projet (`Java-Project`) :
    ```bash
    cd Resto_Asiat/Java-Project
    ```
    *(Ajustez le chemin selon votre dossier d'installation)*

3.  Lancez l'environnement complet avec Docker Compose :
    ```bash
    docker-compose up --build
    ```

4.  **Attendez** que le démarrage soit terminé. Vous devriez voir dans les logs :
    > `projet_backend | ✅ API prête sur http://localhost:7000`
    
    *Laissez ce terminal ouvert.* Le serveur est maintenant opérationnel.

### Étape 2 : Lancer l'Application Client (La Borne)

Le client est une application de bureau JavaFX, il doit être lancé directement sur votre machine (hors Docker) pour gérer l'affichage graphique.

1.  Ouvrez un **nouveau** terminal.
2.  Accédez au dossier du Frontend :
    ```bash
    cd Resto_Asiat/Java-Project/Frontend
    ```

3.  Lancez l'application via Maven :
    ```bash
    mvn clean javafx:run
    ```

4.  La fenêtre de la borne de commande doit s'ouvrir. Vous pouvez maintenant utiliser l'application !

---

## 🛠️ En cas de problème

*   **Erreur "Connection Refused"** : Vérifiez que le terminal de l'Étape 1 est bien lancé et n'affiche pas d'erreurs. Le backend doit être prêt avant de lancer le frontend.
*   **Port occupé** : Le projet utilise le port **3306** (MySQL) et **7000** (API). Assurez-vous qu'aucun autre service n'utilise ces ports (arrêtez vos serveurs MySQL locaux type WAMP/XAMPP si nécessaire).
*   **Affichage** : Si l'interface semble déformée, vérifiez que votre écran est réglé à une échelle de 100% ou agrandissez la fenêtre.

## 📦 Architecture Technique

*   **Base de données** : MySQL 8.0 (Container Docker `projet_mysql`)
*   **Backend** : Java 17 + Javalin (Container Docker `projet_backend`)
*   **Frontend** : Java 17 + JavaFX + Apache HttpClient (Exécution locale)
