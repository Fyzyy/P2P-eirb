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

Un fichier possède une clef unique calculable à l'aide d'outils style md5 qu'il faudra intégrer au système

## TODO

- Parser coté client pour vérifier le format du message
- Réussir à faire un exit pour éteindre le serveur
- Ecoute du client de la reponse du serveur -> doit pouvoir récupérer la réponse du serveur après une demande, puis stocker les infos si besoin (num port et @ des peers à contacter)
- Echanger des infos entre clients
- Interface graphique du serveur et du client
- Finir tests client

- Changer en une pool de pthreads pour éviter le DDOS
- Automate?
- Attention aux readLine au niveau client
