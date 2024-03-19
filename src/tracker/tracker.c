#include <omp.h>
#include "config.h"
#include "parser.h"

#define MAX_PEERS_CONNECTIONS 10
char input[100];
int server_socket;

void quit() {

    reset_tracked_files();
    remove_all_peers();
    remove_list(connectedPeers);
    remove_list(allPeers);
    printf("exit tracker\n");
    close(server_socket);
    exit(EXIT_SUCCESS);
}

void handle_peer_connection(int socket, const char *ip, int port) {
    char buffer[MAX_BUFFER_SIZE];
    ssize_t bytes_received;
    
    PeerInfo* peer = new_peer(connectedPeers, ip, port);

    while ((bytes_received = recv(socket, buffer, sizeof(buffer), 0)) > 0) {
        // Traiter les données reçues
        buffer[bytes_received] = '\0';
        printf("Données reçues de %s:%d : %s\n", ip, port, buffer);
        
        response* res = create_response(peer);
        parsing(buffer, res);
        send(socket, res->message, strlen(res->message), 0);
        //TODO envoyer la réponse
        free(res);
    }

    if (bytes_received == 0) {
        printf("%s:%d déconnecté.\n", ip, port);
        delete_peer_from_list(connectedPeers, ip, port);
        printf("connected peer : %d\n", connectedPeers->n_peers);
    } else if (bytes_received == -1) {
        perror("Erreur lors de la réception de données");
    }

}

void accept_connections(int server_socket) {
    struct sockaddr_in client_address;
    socklen_t client_address_len = sizeof(client_address);

        int client_socket;
        char client_ip[INET_ADDRSTRLEN];

        while (1) {

            if ((client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_len)) == -1) {
                perror("Erreur lors de l'acceptation de la connexion");
                exit(EXIT_FAILURE);
            }
            inet_ntop(AF_INET, &(client_address.sin_addr), client_ip, INET_ADDRSTRLEN);
            printf("Connexion acceptée de %s:%d\n", client_ip, (int) ntohs(client_address.sin_port));
            printf("connected peer : %d\n", connectedPeers->n_peers + 1);

            #pragma omp task firstprivate(client_socket, client_ip, client_address) shared(connectedPeers, allPeers, server_socket)
            {
                handle_peer_connection(client_socket, client_ip, (int) ntohs(client_address.sin_port));
                close(client_socket);
            }
        }

}

int main() {

    struct ServerConfig serverConfig;
    load_config("config.ini", &serverConfig);

    init_global_lists();
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
    puts("'peers' pour afficher la liste des pairs connectés :");
    puts("'files' pour afficher la liste des fichiers suivis :");

    puts("'exit' pour quitter le tracker :");

    #pragma omp parallel
    {
        #pragma omp single
        {
            // Boucle principale
            while (1) {

                #pragma omp task shared(input)
                {
                    while (1) {
                        fgets(input, sizeof(input), stdin);
                        if (strncmp(input, "exit", 4) == 0)
                            quit();
                        if (strncmp(input, "files", 5) == 0)
                            display_tracked_files();
                        if (strncmp(input, "peers", 5) == 0)
                            display_peers(connectedPeers);
                    }
                }

                accept_connections(server_socket);
                fgets(input, sizeof(input), stdin);
                if (strncmp(input, "exit", 4) == 0)
                    quit();
            }
        }
    }
    quit();

    return 0;
}
