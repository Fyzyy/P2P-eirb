#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>

#define MAX_PEERS 50
#define MAX_FILES_PER_PEER 50 
#define MAX_BUFFER_SIZE 2048 


struct PeerInfo {
    int socket;
    char ip_address[INET_ADDRSTRLEN];
    int port;
};

struct PeerInfo connectedPeers[MAX_PEERS];
int numConnectedPeers = 0;

void display_peer_info();