#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#include "parser.h"

char* string_tokens[12] = {
    "announce",
    "listen",
    "seed",
    "leech",
    "look",
    "getfile",
    "list",
    "update",
    "peers",
    "ok",
    "unknown",
    "max"
};

char* token_to_str(enum tokens token) {
    return string_tokens[token];
}

enum tokens str_to_token(char* str) {
    for (int i = 0; i < MAX_TOKEN; i++) {
        if (strcmp(str, string_tokens[i]) == 0) {
            return i;
        }
    }
    return UNKNOWN;
}

enum tokens seed(char* tokens) {


    // Aller à la partie de la liste de fichiers
    tokens = strtok(NULL, "[");
    
    if (tokens != NULL)
        printf("Peer seeding files:\n");

    while (tokens != NULL) {
        char* file_name = strtok(NULL, " "); 
        char* file_size_str = strtok(NULL, " ");
        char* piece_size_str = strtok(NULL, " ");
        char* file_key = strtok(NULL, " ]");

        if (file_name == NULL || file_size_str == NULL || piece_size_str == NULL || file_key == NULL) {
            break;
        }

        int file_size = atoi(file_size_str);
        int piece_size = atoi(piece_size_str);

        printf("File: %s, Size: %d, Piece Size: %d, Key: %s\n", file_name, file_size, piece_size, file_key);
        //TODO
    }
    return OK;
}



enum tokens announce(char* tokens) {
    tokens = strtok(NULL, " "); // listen || seed
    switch (str_to_token(tokens))
    {
    case LISTEN:
        tokens = strtok(NULL, " "); //port
        printf("peer listening %d\n", atoi(tokens)); 
        //TODO
        __attribute__((fallthrough));
    case SEED:
        return seed(tokens);
    case UNKNOWN:
        printf("Unknown command\n");
        return UNKNOWN;
    default:
        return OK;
    }
}

//look [filename="foo" filesize>"100"]
enum tokens look(char* tokens) {
    char temp_copy[256];
    tokens = strtok(NULL, "[");

    printf("Peer looking for files:\n");

    char* filename = strstr(tokens, "filename=");
    if (filename == NULL) {
        printf("Erreur : Clé de fichier manquante.\n");
        return UNKNOWN;
    }
    else {
        filename += strlen("filename=");
        filename = strtok(strncpy(temp_copy, filename, strlen(filename)), "\"");
        printf("Matching files for filename '%s':\n", filename);
    }

    char* filesize = strstr(tokens, "filesize>");
    if (filesize == NULL) {
        printf("Erreur : Clé de taille manquante.\n");
        return UNKNOWN;
    }
    else {
        filesize += strlen("filesize>");
        filesize = strtok(strncpy(temp_copy, filesize, strlen(filesize)), "\"");
        printf("Matching files for filesize > %d:\n", atoi(filesize));
    }
    
    //TODO
    return LIST;
}

enum tokens getfile(char* tokens) {
    tokens = strtok(NULL, " "); // file key
    if (tokens == NULL) {
        printf("Erreur : Clé de fichier manquante.\n");
        return UNKNOWN;
    }
    printf("Peer requesting file with key: %s\n", tokens);
    //TODO

    return PEERS;
}

enum tokens parsing(char* buffer) {
    char* tokens = strtok(buffer, " "); // announce || look || getfile
    switch (str_to_token(tokens))
    {
        case ANNOUNCE:
            return announce(tokens);
        
        case LOOK :
            return look(tokens);
        
        case GETFILE:
            return getfile(tokens);

        case UNKNOWN:
            printf("Unknown command\n");
            return UNKNOWN;
        default:
            return UNKNOWN;
    }
}
