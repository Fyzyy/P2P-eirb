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
#define MAX_FILES_PER_PEER 50  
#define MAX_BUFFER_SIZE 8192


struct PeerInfo {
    int socket;
    char ip_address[INET_ADDRSTRLEN];
    int port;
    char files[MAX_FILES_PER_PEER][MAX_KEY_LENGTH];
    int num_files;
};

struct PeerInfo connectedPeers[MAX_PEERS];
int numConnectedPeers = 0;

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

void handle_peer_connection(int socket, const char *ip, int port) {

    char buffer[MAX_BUFFER_SIZE];
    ssize_t bytes_received;

    while ((bytes_received = recv(socket, buffer, sizeof(buffer), 0)) > 0) {
        // Traiter les données reçues
        // ...

        buffer[bytes_received] = '\0';
        printf("Données reçues de %s:%d : %s\n", ip, port, buffer);
    }

    if (bytes_received == 0) {
        printf("%s:%d déconnecté.\n", ip, port);
    } else if (bytes_received == -1) {
        perror("Erreur lors de la réception de données");
    }

    close(socket);
}

int main() {

    struct ServerConfig serverConfig;
    load_config("config.ini", &serverConfig);

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
    server_address.sin_addr.s_addr = inet_addr(serverConfig.address);
    server_address.sin_port = htons(serverConfig.port);

    // Lier la socket à l'adresse et au port
    if (bind(server_socket, (struct sockaddr *)&server_address, sizeof(server_address)) == -1) {
        perror("Erreur lors de la liaison de la socket");
        exit(EXIT_FAILURE);
    }

    if (listen(server_socket, 10) == -1) {
        perror("Erreur lors de la mise en écoute de la socket");
        exit(EXIT_FAILURE);
    }

    printf("Serveur en attente de connexions sur le port %d...\n", serverConfig.port);

    while (1) {
        if ((client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_len)) == -1) {
            perror("Erreur lors de l'acceptation de la connexion");
            exit(EXIT_FAILURE);
        }

        char client_ip[INET_ADDRSTRLEN];
        inet_ntop(AF_INET, &(client_address.sin_addr), client_ip, INET_ADDRSTRLEN);

        printf("Connexion acceptée de %s:%d\n", client_ip, ntohs(client_address.sin_port));

        handle_peer_connection(client_socket, client_ip, client_address.sin_port);
    }

    close(server_socket);

    return 0;
}
