# Sujet projet RE218: Groupe 3

## Résumé du fonctionnement

voir images/image.png

## Parties du projet

### Serveur (Tracker)

Langage: C

### Client

Langage: objet --> Java

### Application

Langage: JS?

## Infos

- Un fichier possède une clef unique calculable à l'aide d'outils style md5 qu'il faudra intégrer au système
- si un message est mal formaté la connexion doit se fermer au bout de 3 tentatives d’erreur de suite.
- la connexion TCP ne doit pas se fermer à chaque réception de message.
- ChatGPT ou autre LLM sont autorisés pour trouver des bouts de code principalement sur le parsing des commandes. Un référencement approprié est demandé. De même, un retour d'expérience tracé dans le rapport intermédiaire ou finale est demandé.
- suppression de la partie distribuée et de blockchain. Ce projet se concentre seulement sur la partie centralisée.
- Pour chaque fichier en cours de téléchargement, il est nécessaire de stocker un manifest (fichier metadata) qui sauvegarde l’état de téléchargement dont la clé du fichier, la taille d’une pièce, etc.

## TODO

- Ecoute du client de la reponse du serveur -> doit pouvoir stocker les infos du tracker (num port et @ des peers à contacter)
- Interface graphique du serveur et du client
- Echange de fichiers
- Stocker les infos sur les fichiers/échanges dans des fichiers temporaires
- Informer les voisins de l'état de l'avancement des téléchargements

### Gestion des échanges de fichiers

- Stocker les données recues -> dans un nouveau fichier avec le bon nom
- Paramétrer la taille des messages recus dans le fichiers de config

Proposition de gestion:

- Dès qu'on demande un look et qu'on recoit une réponse positive, on créé le fichier et on créé un dictionnaire où stocker les associations clefs/noms de fichier

### Interface

- Pouvoir se connecter et demander des infos au tracker sans avoir à rentrer toute la ligne de commande
- Pouvoir faire pareil avec un autre peer
