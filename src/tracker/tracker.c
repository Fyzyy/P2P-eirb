#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#include "config.h"

#define PORT 8080
#define BUFFER_SIZE 1024
#define MAX_PEERS 10
#define MAX_KEY_LENGTH 33  // Longueur maximale d'une clé MD5 (32 caractères + 1 pour le caractère nul)
#define MAX_FILES_PER_PEER 50  // Nombre maximal de fichiers par pair

// Structure pour stocker les informations sur les pairs
struct PeerInfo {
    int socket;
    char ip_address[INET_ADDRSTRLEN];  // pour stocker l'adresse IP au format texte
    int port;
    char files[MAX_FILES_PER_PEER][MAX_KEY_LENGTH];
    int num_files;
    // Ajoutez d'autres informations si nécessaire
};

// Tableau pour stocker les informations sur les pairs connectés
struct PeerInfo connectedPeers[MAX_PEERS];
int numConnectedPeers = 0;
/*
// Fonction pour gérer les demandes de fichiers
void handle_file_request(int client_socket, const char *file_key) {
    // ... (recherche des pairs ayant la clé correspondante)

    // Envoyer la réponse au pair approprié
    send(connectedPeers[peerIndex].socket, file_key, strlen(file_key), 0);
}

// Fonction pour stocker des logs
void log_message(const char *message) {
    // ... (écriture dans un fichier de logs, affichage à la console, etc.)
    printf("%s\n", message);
}

// Fonction pour charger la configuration depuis un fichier
void load_configuration(const char *config_file) {
    // ... (chargement de la configuration)
}
*/

// Fonction pour afficher les informations sur les pairs
void display_peer_info() {
    for (int i = 0; i < numConnectedPeers; ++i) {
        printf("Peer %d:\n", i + 1);
        printf("  IP Address: %s\n", connectedPeers[i].ip_address);
        printf("  Port: %d\n", connectedPeers[i].port);
        printf("  Files:\n");
        for (int j = 0; j < connectedPeers[i].num_files; ++j) {
            printf("    %s\n", connectedPeers[i].files[j]);
        }
        printf("\n");
    }
}

// Fonction pour gérer les connexions entrantes des pairs
void handle_peer_connection(int peer_socket, const char *peer_ip, int peer_port) {
    // ... (traitement initial des connexions peer)

    // Stocker les informations sur le pair
    if (numConnectedPeers < MAX_PEERS) {
        struct PeerInfo newPeer;
        newPeer.socket = peer_socket;
        strncpy(newPeer.ip_address, peer_ip, INET_ADDRSTRLEN);
        newPeer.port = peer_port;
        newPeer.num_files = 0;
        // Ajoutez d'autres informations si nécessaire
        connectedPeers[numConnectedPeers] = newPeer;
        numConnectedPeers++;
    } else {
        // Gérer le dépassement de la limite des pairs connectés
        // (vous pouvez le gérer selon vos besoins)
    }
    display_peer_info();
    // ... (traitement ultérieur des connexions peer)
}

// Fonction pour ajouter un fichier à la liste des fichiers d'un pair
void add_file_to_peer(int peer_index, const char *file_key) {
    struct PeerInfo *peer = &connectedPeers[peer_index];
    
    if (peer->num_files < MAX_FILES_PER_PEER) {
        strncpy(peer->files[peer->num_files], file_key, MAX_KEY_LENGTH);
        peer->num_files++;
    } else {
        // Gérer le dépassement de la limite des fichiers par pair
        // (vous pouvez le gérer selon vos besoins)
    }
}

int main() {

    struct ServerConfig serverConfig;

    if (load_config("config.ini", &serverConfig) != 0) {
        fprintf(stderr, "Failed to load configurations.\n");
        return 1;
    }

    printf("Server Configurations:\n");
    printf("Port: %d\n", serverConfig.port);
    printf("Address: %s\n", serverConfig.address);


    int server_socket, client_socket;
    struct sockaddr_in server_address, client_address;
    socklen_t client_address_len = sizeof(client_address);

    // Créer une socket
    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Erreur lors de la création de la socket");
        exit(EXIT_FAILURE);
    }

    // Configurer l'adresse du serveur
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(PORT);

    // Lier la socket à l'adresse et au port
    if (bind(server_socket, (struct sockaddr *)&server_address, sizeof(server_address)) == -1) {
        perror("Erreur lors de la liaison de la socket");
        exit(EXIT_FAILURE);
    }

    // Mettre le serveur en mode écoute
    if (listen(server_socket, 10) == -1) {
        perror("Erreur lors de la mise en écoute de la socket");
        exit(EXIT_FAILURE);
    }

    printf("Serveur en attente de connexions sur le port %d...\n", PORT);

    while (1) {
        // Accepter la connexion entrante
        if ((client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_len)) == -1) {
            perror("Erreur lors de l'acceptation de la connexion");
            exit(EXIT_FAILURE);
        }

        char client_ip[INET_ADDRSTRLEN];
        inet_ntop(AF_INET, &(client_address.sin_addr), client_ip, INET_ADDRSTRLEN);

        printf("Connexion acceptée de %s:%d\n", client_ip, ntohs(client_address.sin_port));

        // Gérer la requête du client
        handle_peer_connection(client_socket, client_ip, client_address.sin_port);
    }

    // Fermer la socket du serveur
    close(server_socket);

    return 0;
}
