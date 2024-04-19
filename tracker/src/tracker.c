#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <pthread.h>
#include <errno.h>
#include <sys/select.h>
#include "config.h"
#include "parser.h"
#include "thpool.h"

#define MAX_PEERS_CONNECTIONS 1000
#define NB_THREADS 5

typedef struct args_data {
    char buffer[MAX_BUFFER_SIZE];
    PeerInfo* peer;
}args_data;

char input[100];
int server_socket;
threadpool thpool;

void quit() {
    reset_tracked_files();
    remove_list(connectedPeers);
    remove_list(allPeers);
    printf("Sortie du tracker\n");
    close(server_socket);
    exit(EXIT_SUCCESS);
}

void disconnect_peer(PeerInfo * peer) {
    printf("%s:%d déconnecté.\n", peer->ip_address, peer->port);
    delete_peer_from_list(connectedPeers, peer->ip_address, peer->port);
    close(peer->socket);
    printf("Nombre de pairs connectés : %d\n", connectedPeers->n_peers);
}

void parse_and_response(void* args) {
    args_data* data = (args_data*)args;
    char* buffer = data->buffer;
    PeerInfo* peer = data->peer;
    int must_disconnect = 0;

    printf("Données reçues de %s:%d : %s\n", peer->ip_address, peer->port, buffer);
    response* res = create_response(peer);
    if (parsing(buffer, res) == UNKNOWN) {
        peer->nb_incorrect_cmds += 1;
    }
    else {
        peer->nb_incorrect_cmds = 0;
    }
    if (peer->nb_incorrect_cmds >= 3) {
        must_disconnect = 1;
        //printf("Je dois te déco mais vsy je sais pas encore faire\n");
    }
    
    int bytesend = send(peer->socket, res->message, strlen(res->message), 0);
    if (bytesend == -1) {
        perror("Erreur lors de l'envoi de données");
    }
    printf("Données envoyées à %s:%d : %s\n", peer->ip_address, peer->port, res->message);
    free(res);
    free(data);
    if (must_disconnect) {
        disconnect_peer(peer);
    }
}

void* handle_data() {
    while (1) {
        fd_set readfds;
        int max_fd = 0;

        FD_ZERO(&readfds);

        for (int i = 0; i < connectedPeers->n_peers; i++) {
            int sockfd = connectedPeers->peers[i]->socket;
            FD_SET(sockfd, &readfds);

            if (sockfd > max_fd) {
                max_fd = sockfd;
            }
        }

        struct timeval timeout;
        timeout.tv_sec = 0;
        timeout.tv_usec = 1000;  // Timeout de 1 milliseconde

        int activity = select(max_fd + 1, &readfds, NULL, NULL, &timeout);

        if (activity < 0) {
            perror("Erreur lors de l'utilisation de select");
            continue;
        }

        for (int i = 0; i < connectedPeers->n_peers; i++) {
            int sockfd = connectedPeers->peers[i]->socket;

            if (FD_ISSET(sockfd, &readfds)) {
                ssize_t bytes_received;
                char buffer[MAX_BUFFER_SIZE];
                bytes_received = recv(sockfd, buffer, sizeof(buffer), 0);

                if (bytes_received > 0) {
                    args_data* args = (args_data*) malloc(sizeof(args_data));
                    strcpy(args->buffer, buffer);
                    args->peer = connectedPeers->peers[i];
                    args->buffer[bytes_received] = '\0';
                    thpool_add_work(thpool, parse_and_response, (void*) args);
                } else if (bytes_received == 0) {
                    disconnect_peer(connectedPeers->peers[i]);
                } else if (bytes_received == -1) {
                    perror("Erreur lors de la réception de données");
                }
            }
        }
    }
}


void* handle_client() {
    while (1) {
        int client_socket;
        struct sockaddr_in client_address;
        socklen_t client_address_len = sizeof(client_address);
        
        char client_ip[INET_ADDRSTRLEN];
        int port;

        client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_len);
        
        inet_ntop(AF_INET, &(client_address.sin_addr), client_ip, INET_ADDRSTRLEN);
        port = (int) ntohs(client_address.sin_port);

        PeerInfo* peer = new_peer(connectedPeers, client_ip, port);
        peer->socket = client_socket;

        printf("Connexion acceptée de %s:%d\n", client_ip, port);
        printf("Nombre de pairs connectés : %d\n", connectedPeers->n_peers);
    }
}

void* handle_stdin() {
    while (1) {
        fgets(input, sizeof(input), stdin);
        if (strncmp(input, "exit", 4) == 0)
            pthread_exit(NULL);
        if (strncmp(input, "files", 5) == 0)
            display_tracked_files();
        if (strncmp(input, "peers", 5) == 0)
            display_connected_peer_info();
    }
}

int main() {
    struct ServerConfig serverConfig;
    if (load_config("config.ini", &serverConfig) != 0) {
        return EXIT_FAILURE;
    }

    init_global_lists();
    struct sockaddr_in server_address;



    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Erreur lors de la création de la socket");
        exit(EXIT_FAILURE);
    }

    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = inet_addr(serverConfig.address);
    server_address.sin_port = htons(serverConfig.port);

    if (bind(server_socket, (struct sockaddr *)&server_address, sizeof(server_address)) == -1) {
        perror("Erreur lors de la liaison de la socket");
        exit(EXIT_FAILURE);
    }

    if (listen(server_socket, MAX_PEERS_CONNECTIONS) == -1) {
        perror("Erreur lors de la mise en écoute de la socket");
        exit(EXIT_FAILURE);
    }

    printf("Serveur en attente de connexions sur le port %d...\n", serverConfig.port);
    puts("'peers' pour afficher la liste des pairs connectés :");
    puts("'files' pour afficher la liste des fichiers suivis :");
    puts("'exit' pour quitter le tracker :");


    thpool = thpool_init(NB_THREADS);

    pthread_t input, client, data;
    pthread_create(&input, NULL, handle_stdin, NULL);
    pthread_create(&client, NULL, handle_client, NULL);
    pthread_create(&data, NULL, handle_data, NULL);

    pthread_join(input, NULL);

    pthread_cancel(client);
    pthread_cancel(data);
    thpool_destroy(thpool);    


    quit();

    return 0;
}
