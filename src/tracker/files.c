#include "files.h"
#include <string.h>
#include <stdio.h>

FileInfo* trackedFiles = NULL;

// Fonction pour ajouter un fichier à la liste des fichiers suivis
void add_tracked_file(const char* filename, int length, int pieceSize, const char* key) {
    FileInfo* newFile = malloc(sizeof(FileInfo));
    if (newFile != NULL) {
        strncpy(newFile->filename, filename, MAX_FILENAME_SIZE - 1);
        newFile->filename[MAX_FILENAME_SIZE - 1] = '\0';
        newFile->length = length;
        newFile->pieceSize = pieceSize;
        strncpy(newFile->key, key, MAX_KEY_LENGTH - 1);
        newFile->key[MAX_KEY_LENGTH - 1] = '\0';

        newFile->seeder = create_peers_list();
        newFile->leecher = create_peers_list();
        newFile->seeder->n_peers = 0;
        newFile->leecher->n_peers = 0;
        
 
        newFile->next = trackedFiles;

        trackedFiles = newFile;
    }
}

// Fonction pour rechercher un fichier dans la liste des fichiers suivis
FileInfo* search_tracked_file(const char* key) {
    FileInfo* current = trackedFiles;
    while (current != NULL) {
        if (strcmp(current->key, key) == 0) {
            return current;
        }
        current = current->next;
    }
    return NULL;
}

void remove_tracked_file(const char* key) {
    FileInfo* current = trackedFiles;
    FileInfo* previous = NULL;

    while (current != NULL) {
        if (strcmp(current->key, key) == 0) {
            if (previous != NULL) {
                previous->next = current->next;
            } else {
                trackedFiles = current->next;
            }
            free(current);
            return;
        }
        previous = current;
        current = current->next;
    }
}


// Fonction pour ajouter un pair seeder à un fichier suivi
int add_seeder_to_tracked_file(const char* key, const char* ip, int port) {
    FileInfo* file = search_tracked_file(key);
    if (file != NULL) {
        PeerInfo* newPeer = add_peer(file->seeder, ip, port);
        return (newPeer != NULL) ? 1 : 0;
    }
    return 0;
}

// Fonction pour ajouter un pair leecher à un fichier suivi
int add_leecher_to_tracked_file(const char* key, const char* ip, int port) {
    FileInfo* file = search_tracked_file(key);
    if (file != NULL) {
        PeerInfo* newPeer = add_peer(file->leecher, ip, port);
        return (newPeer != NULL) ? 1 : 0;
    }
    return 0;
}

// Fonction pour supprimer un pair seeder d'un fichier suivi
int remove_seeder_from_tracked_file(const char* key, const char* ip, int port) {
    FileInfo* file = search_tracked_file(key);
    if (file != NULL) {
        delete_peer_from_list(trackedFiles->seeder,ip, port);
        return 1;
    }
    return 0;
}

// Fonction pour supprimer un pair leecher d'un fichier suivi
int remove_leecher_from_tracked_file(const char* key, const char* ip, int port) {
    FileInfo* file = search_tracked_file(key);
    if (file != NULL) {
        delete_peer_from_list(trackedFiles->leecher,ip, port);
        return 1;
    }
    return 0;
}

void reset_tracked_files() {
    FileInfo* current = trackedFiles;
    while (current != NULL) {
        FileInfo* next = current->next;
        free(current);
        current = next;
    }
    trackedFiles = NULL;
}

// Fonction pour afficher les pairs seeder et leecher d'un fichier suivi
void display_peers_for_tracked_file(const char* key) {
    FileInfo* file = search_tracked_file(key);
    if (file != NULL) {
        printf("Seeder peers for file %s:\n", file->filename);
        display_peers(file->seeder);
        printf("Leecher peers for file %s:\n", file->filename);
        display_peers(file->leecher);
    }
}

// Fonction pour afficher tous les fichiers suivis
void display_tracked_files() {
    FileInfo* current = trackedFiles;
    while (current != NULL) {
        printf("File: %s |", current->filename);
        printf("Key: %s |", current->key);
        printf("Length: %d |", current->length);
        printf("Piece size: %d |", current->pieceSize);
        printf("Seeder peers: |");
        display_peers(current->seeder);
        printf("Leecher peers:\n");
        display_peers(current->leecher);
        current = current->next;
    }
}