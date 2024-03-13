#ifndef FILES_H
#define FILES_H

#include "peer.h"


#define MAX_FILES 100
#define MAX_FILENAME_SIZE 1024
#define MAX_KEY_LENGTH 33  // Longueur maximale d'une clé MD5 (32 caractères + 1 pour le caractère nul)

typedef struct {
    char filename[MAX_FILENAME_SIZE];
    int length;
    int pieceSize;
    char key[MAX_KEY_LENGTH];
} FileInfo;

typedef struct {
    FileInfo files[MAX_FILES];
    int fileCount;
} FileData;

void addFile(FileData *fileData, char *filename, int length, int pieceSize, char *key);
void searchFiles(FileData *fileData, char *criteria);

#endif /* FILES_H */
