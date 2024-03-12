#include "files.h"
#include <stdio.h>
#include <string.h>

void addFile(FileData *fileData, char *filename, int length, int pieceSize, char *key) {
    if (fileData->fileCount < MAX_FILES) {
        FileInfo *file = &(fileData->files[fileData->fileCount]);
        strcpy(file->filename, filename);
        file->length = length;
        file->pieceSize = pieceSize;
        strcpy(file->key, key);
        fileData->fileCount++;
    } else {
        printf("Tracker capacity exceeded for files.\n");
    }
}

void searchFiles(FileData *fileData, char *criteria) {
    // Implémentez la logique de recherche ici en fonction des critères fournis
    // Cette fonction devrait parcourir les fichiers sur le tracker et répondre avec les fichiers correspondants
}
