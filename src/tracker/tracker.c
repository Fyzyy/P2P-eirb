#include <omp.h>

#include "config.h"
#include "parser.h"

#define MAX_PEERS_CONNECTIONS 10

void handle_peer_connection(int socket, const char *ip, int port) {

    char buffer[MAX_BUFFER_SIZE];
    ssize_t bytes_received;

    while ((bytes_received = recv(socket, buffer, sizeof(buffer), 0)) > 0) {
        // Traiter les données reçues

        buffer[bytes_received] = '\0';
        printf("Données reçues de %s:%d : %s\n", ip, port, buffer);
        parsing(buffer, ip, port);

    }

    if (bytes_received == 0) {
        printf("%s:%d déconnecté.\n", ip, port);
        #pragma omp critical
            connectedPeers.n_peers--;
        printf("connected peer : %d\n", connectedPeers.n_peers);
        
    } else if (bytes_received == -1) {
        perror("Erreur lors de la réception de données");
    }

    close(socket);
}

void accept_connections(int server_socket) {
    struct sockaddr_in client_address;
    socklen_t client_address_len = sizeof(client_address);
    int client_socket;

    #pragma omp parallel for //threads
    for (size_t i = 0; i < MAX_PEERS_CONNECTIONS; i++) {
        if ((client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_len)) == -1) {
            perror("Erreur lors de l'acceptation de la connexion");
            exit(EXIT_FAILURE);
        }
        #pragma omp critical
            connectedPeers.n_peers++;
        printf("connected peer : %d\n", connectedPeers.n_peers);

        char client_ip[INET_ADDRSTRLEN];
        inet_ntop(AF_INET, &(client_address.sin_addr), client_ip, INET_ADDRSTRLEN);
        printf("Connexion acceptée de %s:%d\n", client_ip, ntohs(client_address.sin_port));

        handle_peer_connection(client_socket, client_ip, client_address.sin_port);

        close(client_socket);
    }
}

int main() {

    struct ServerConfig serverConfig;
    load_config("config.ini", &serverConfig);

    int server_socket;
    struct sockaddr_in server_address;

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

    if (listen(server_socket, MAX_PEERS_CONNECTIONS) == -1) {
        perror("Erreur lors de la mise en écoute de la socket");
        exit(EXIT_FAILURE);
    }

    printf("Serveur en attente de connexions sur le port %d...\n", serverConfig.port);

    while (1) {
        accept_connections(server_socket);
    }
        

    close(server_socket);

    return 0;
}
