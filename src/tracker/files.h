#ifndef FILES_H
#define FILES_H

#include "peer.h"

#define MAX_FILES 100
#define MAX_FILENAME_SIZE 1024
#define MAX_KEY_LENGTH 33  // Longueur maximale d'une clé MD5 (32 caractères + 1 pour le caractère nul)

typedef struct FileInfo{
    char filename[MAX_FILENAME_SIZE];
    int length;
    int pieceSize;
    char key[MAX_KEY_LENGTH];
    PeersList* seeder; // Liste des pairs semant le fichier
    PeersList* leecher; // Liste des pairs téléchargeant le fichier
    struct FileInfo* next; // Pointeur vers le prochain FileInfo dans la liste chaînée
} FileInfo;

extern FileInfo* trackedFiles; // Déclaration de la liste chaînée de fichiers suivis

// Ajoute un fichier à la liste des fichiers suivis
void add_tracked_file(const char* filename, int length, int pieceSize, const char* key);

// Supprime un fichier de la liste des fichiers suivis
void remove_tracked_file(const char* key);

// Recherche un fichier dans la liste des fichiers suivis
FileInfo* search_tracked_file(const char* key);

// Ajoute un pair seeder à un fichier suivi
int add_seeder_to_tracked_file(const char* key, const char* ip, int port);

// Ajoute un pair leecher à un fichier suivi
int add_leecher_to_tracked_file(const char* key, const char* ip, int port);

// Supprime un pair seeder d'un fichier suivi
int delete_seeder_from_tracked_file(const char* key, const char* ip, int port);

// Supprime un pair leecher d'un fichier suivi
int delete_leecher_from_tracked_file(const char* key, const char* ip, int port);

// Affiche les pairs seeder et leecher d'un fichier suivi
void display_peers_for_tracked_file(const char* key);

void reset_tracked_files();

// Affiche les fichiers suivis
void display_tracked_files();

#endif /* FILES_H */
