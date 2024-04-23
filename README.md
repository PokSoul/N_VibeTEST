# Application de recherche d'itinéraires

Cette application Android permet aux utilisateurs de rechercher des itinéraires entre deux points géographiques en utilisant l'API Google Directions.

## Architecture

L'application suit une architecture simplifiée basée sur le modèle MVC (Modèle-Vue-Contrôleur) :

- **Modèle (Model) :**
  - La classe `DirectionService` encapsule la logique métier pour interagir avec l'API Google Directions et obtenir des itinéraires entre deux points.

- **Vue (View) :**
  - Les éléments de l'interface utilisateur sont définis dans le fichier `activity_main.xml`, y compris les champs de saisie, le bouton de recherche et les messages affichés à l'utilisateur.

- **Contrôleur (Controller) :**
  - La classe `MainActivity` agit comme le contrôleur, gérant les interactions utilisateur et coordonnant les actions à effectuer en réponse à ces interactions.

## Fonctionnalités

- Saisie d'adresses de départ et d'arrivée.
- Recherche d'itinéraires entre les deux adresses en utilisant l'API Google Directions.
- Affichage de l'itinéraire sur une carte Google Maps.
- Affichage de la durée estimée du trajet.

## Utilisation

1. Clonez le dépôt vers votre environnement de développement Android.
2. Ouvrez le projet dans Android Studio.
3. Configurez votre clé API Google Maps dans le fichier `strings.xml`.
4. Compilez et exécutez l'application sur un émulateur Android ou un appareil physique.

## Configuration requise

- Android Studio 4.0 ou version ultérieure.
- Clé API Google Maps valide.

## Remarque

Assurez-vous de respecter les conditions d'utilisation de l'API Google Directions et d'autres services utilisés dans cette application.
