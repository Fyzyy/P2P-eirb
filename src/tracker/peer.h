#ifndef PEER_H
#define PEER_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>

#define MAX_PEERS 100
#define MAX_BUFFER_SIZE 2048

typedef struct {
    char ip_address[INET_ADDRSTRLEN];
    int port;
} PeerInfo;

typedef struct {
    PeerInfo* peers[MAX_PEERS];
    int n_peers;
} PeersList;

extern PeersList* allPeers;
extern PeersList* connectedPeers;


PeerInfo* search_peer(PeersList* peers, const char *ip, int port);

PeersList* create_peers_list();
void init_global_lists();

PeerInfo* new_peer(PeersList* peers, const char *ip, int port);
PeerInfo* delete_peer_from_list(PeersList* peers, const char *ip, int port);

void remove_peer(PeerInfo* peer);
void remove_all_peers();
void remove_list(PeersList* peers);

char* PeersList_to_string(PeersList* peers);

void display_peers(PeersList* peers);
void display_connected_peer_info();

#endif /* PEER_H */
