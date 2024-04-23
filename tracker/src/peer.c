#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "peer.h"

PeersList* allPeers;
PeersList* connectedPeers;

PeerInfo* search_peer(PeersList* peers, const char *ip, int port) {
    PeerInfo* peer;
    for (int i = 0; i < peers->n_peers; i++) {
        peer = peers->peers[i];
        if (strcmp(peer->ip_address, ip) == 0 && peer->port == port) {
            return peer;
        }
    }
    return NULL;
}

PeersList* create_peers_list() {
    PeersList* peers = malloc(1 * sizeof(PeersList));
    if (peers == NULL) {
        fprintf(stderr, "Failed to allocate memory for peers list\n");
        exit(EXIT_FAILURE);
    }

    peers->n_peers = 0;
    return peers;
}

void init_global_lists() {
    allPeers = create_peers_list();
    connectedPeers = create_peers_list();
}

/*
* allocate and create a new peer and add it to the list
* if the list is NULL, add the peer to the allPeers list if it is not already there
* If the peer is already in the list, return the peer
* If the maximum number of peers has been reached, return NULL
*/
PeerInfo* new_peer(PeersList* peers, const char *ip, int port) {
    if (peers == NULL)
        peers = allPeers;

    // Check if the peer is already in the list
    PeerInfo* search = search_peer(allPeers, ip, port);
    if (search != NULL && search_peer(peers, ip, port) == NULL) {
        // Add the peer to the list
        peers->peers[peers->n_peers++] = search;
        return search;
    }
    


    // Check if the maximum number of peers has been reached
    if (peers->n_peers >= MAX_PEERS) {
        fprintf(stderr, "Maximum number of peers reached\n");
        return NULL;
    }

    // malloc is only needed when adding a new peer
    PeerInfo* newPeer = malloc(sizeof(PeerInfo));
    if (newPeer == NULL) {
        fprintf(stderr, "Failed to allocate memory for new peer\n");
        return NULL;
    }
    
    // Initialize the new peer
    strncpy(newPeer->ip_address, ip, INET_ADDRSTRLEN);
    newPeer->port = port;
    newPeer->listening_port = port;
    newPeer->socket = -1;
    newPeer->nb_incorrect_cmds = 0;
    // Add the peer to the list
    peers->peers[peers->n_peers++] = newPeer;
    
    if (peers != allPeers)
        allPeers->peers[allPeers->n_peers++] = newPeer;

    return newPeer;
}

// Remove a peer from the list (Not the memory of the peer itself, just the list entry)
PeerInfo* delete_peer_from_list(PeersList* peers, const char *ip, int port) {
    for (int i = 0; i < peers->n_peers; i++) {
        PeerInfo* peer = peers->peers[i];
        if (strcmp(peer->ip_address, ip) == 0 && peer->port == port) {
            peers->n_peers--;
            PeerInfo* deletedPeer = peers->peers[i];
            peers->peers[i] = peers->peers[peers->n_peers];
            return deletedPeer;
        }
    }
    return NULL;
}

// Remove a peer from the allPeers list and free the memory
void remove_peer(PeerInfo* peer) {
    free(delete_peer_from_list(allPeers, peer->ip_address, peer->port));
}

//remove all peers from the allPeers list
void remove_all_peers() {
    for (int i = 0; i < allPeers->n_peers; i++) {
        free(allPeers->peers[i]);
    }
    allPeers->n_peers = 0;
}

// Free the memory allocated for the peers list (not the memory of the peers themselves)
void remove_list(PeersList* peersList) {
    if (peersList == allPeers) 
        remove_all_peers();
    free(peersList);
}

char* PeersList_to_string(PeersList* peers) {
    // Allocate initial memory for the string
    size_t initialSize = 1; // Initial size is 1 for the null terminator
    char* str = malloc(initialSize * sizeof(char));
    if (str == NULL) {
        fprintf(stderr, "Failed to allocate memory for string\n");
        exit(EXIT_FAILURE);
    }
    str[0] = '\0';

    if (peers->n_peers == 0) {
        return str;
    }
    
    // Calculate the total size needed for all peer strings
    size_t totalSize = 0;
    for (int i = 0; i < peers->n_peers; i++) {
        totalSize += snprintf(NULL, 0, "%s:%d\n", peers->peers[i]->ip_address, peers->peers[i]->listening_port) + 1; // +1 for null terminator
    }

    // Reallocate memory to accommodate all peer strings
    str = realloc(str, (initialSize + totalSize) * sizeof(char));
    if (str == NULL) {
        fprintf(stderr, "Failed to allocate memory for string\n");
        exit(EXIT_FAILURE);
    }

    // Concatenate peer strings
    for (int i = 0; i < peers->n_peers-1; i++) {
        snprintf(str + strlen(str), totalSize, "%s:%d ", peers->peers[i]->ip_address, peers->peers[i]->listening_port);
    }
    //remove last space
    snprintf(str + strlen(str), totalSize, "%s:%d", peers->peers[peers->n_peers-1]->ip_address, peers->peers[peers->n_peers-1]->listening_port);

    return str;
}



void display_peers(PeersList* peers) {
    char* str = PeersList_to_string(peers);
    printf("Peers List: %s\n", str);
    free(str);
}

void display_connected_peer_info() {
    printf("Connected Peers:\n");
    display_peers(connectedPeers);
}