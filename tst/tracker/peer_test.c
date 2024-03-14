#include "../../src/tracker/peer.h"
#include "peer_test.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <assert.h>

#define YELLOW_TEXT(text) "\033[0;33m" text "\033[0m"
#define GREEN_TEXT(text) "\033[0;32m" text "\033[0m\n"

// Fonction de test pour la création d'une liste de pairs
void test_create_peers_list() {
    PeersList* peers = create_peers_list();
    assert(peers != NULL && "Failed to create peers list.");
    remove_list(peers);
}

// Fonction de test pour la recherche de pairs dans une liste
void test_search_peer() {
    PeersList* peers = create_peers_list();
    add_peer(peers, "192.168.0.1", 8080);
    PeerInfo* foundPeer = search_peer(peers, "192.168.0.1", 8080);
    assert(foundPeer != NULL && "Peer not found.");
    remove_list(peers);
}

// Fonction de test pour l'ajout d'un pair dans une liste
void test_add_peer() {
    PeersList* peers = create_peers_list();
    PeerInfo* peer1 = add_peer(peers, "192.168.0.1", 8080);
    assert(peer1 != NULL && "Failed to add peer.");
    remove_list(peers);
}

// Fonction de test pour la suppression d'un pair de la liste
void test_delete_peer_from_list() {
    PeersList* peers = create_peers_list();
    add_peer(peers, "192.168.0.1", 8080);
    PeerInfo* deletedPeer = delete_peer_from_list(peers, "192.168.0.1", 8080);
    assert(deletedPeer != NULL && "Peer not found for deletion.");
    remove_list(peers);
}

// Fonction de test pour la suppression de tous les pairs de la liste
void test_remove_all_peers() {
    PeersList* peers = create_peers_list();
    add_peer(peers, "192.168.0.1", 8080);
    add_peer(peers, "192.168.0.2", 8081);
    remove_all_peers();
    assert(allPeers.n_peers == 0);
}


void all_tests_peer() {
    puts(YELLOW_TEXT("Testing peer functions..."));
    test_create_peers_list();
    test_search_peer();
    test_add_peer();
    test_delete_peer_from_list();
    test_remove_all_peers();
    printf(GREEN_TEXT("All tests on peer passed successfully!\n"));
}

