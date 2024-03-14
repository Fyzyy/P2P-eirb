#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "peer.h"

PeersList allPeers;
PeersList connectedPeers;

PeerInfo* search_peer(PeersList* peers, const char *ip, int port) {
    for (int i = 0; i < peers->n_peers; i++) {
        PeerInfo* peer = peers->peers[i];
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

PeerInfo* add_peer(PeersList* peers, const char *ip, int port) {

    // Check if the peer is already in the list
    if (search_peer(peers, ip, port) != NULL) {
        return search_peer(peers, ip, port);
    }

    // Check if the maximum number of peers has been reached
    if (peers->n_peers >= MAX_PEERS) {
        fprintf(stderr, "Maximum number of peers reached\n");
        return NULL;
    }
    
    // malloc
    PeerInfo* newPeer = malloc(sizeof(PeerInfo));
    if (newPeer == NULL) {
        fprintf(stderr, "Failed to allocate memory for new peer\n");
        return NULL;
    }
    strncpy(newPeer->ip_address, ip, INET_ADDRSTRLEN);
    newPeer->port = port;

    /* 
        Add the peer to the list
        If the list is NULL, then add the peer to the allPeers list if it is not already there
    */
    if (peers != NULL) {
        peers->peers[peers->n_peers] = newPeer;
        peers->n_peers++;
    }

    if (search_peer(&allPeers, ip, port) == NULL) {
        allPeers.peers[allPeers.n_peers] = newPeer;
        allPeers.n_peers++;
        return newPeer;
    }
    else if (peers == NULL) {
        free(newPeer);
        return search_peer(&allPeers, ip, port);
    }

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
    free(delete_peer_from_list(&allPeers, peer->ip_address, peer->port));
}

//remove all peers from the allPeers list
void remove_all_peers() {
    for (int i = 0; i < allPeers.n_peers; i++) {
        free(allPeers.peers[i]);
    }
    allPeers.n_peers = 0;
}

// Free the memory allocated for the peers list (not the memory of the peers themselves)
void remove_list(PeersList* peers) {
    free(peers->peers);
}



void display_peers(PeersList* peers) {
    printf("Peers List:\n");
    for (int i = 0; i < peers->n_peers; i++) {
        PeerInfo* peer = peers->peers[i];
        printf("IP: %s, Port: %d\n", peer->ip_address, peer->port);
    }
}

void display_connected_peer_info() {
    printf("Connected Peers:\n");
    display_peers(&connectedPeers);
}