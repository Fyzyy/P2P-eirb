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

void seed(char* tokens) {
    printf("Peer seeding files:\n");

    // Aller à la partie de la liste de fichiers
    tokens = strtok(NULL, "[");
    if (tokens == NULL) {
        printf("Erreur : Crochet ouvrant manquant.\n");
        return;
    }

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
    }
}



void announce(char* tokens) {
    tokens = strtok(NULL, " ");
    switch (str_to_token(tokens))
    {
    case LISTEN:
        tokens = strtok(NULL, " ");
        printf("peer listening %d\n", atoi(tokens)); 
        __attribute__((fallthrough));
    case SEED:
        seed(tokens);
        break;
    default:
        break;
    }
}

enum tokens parsing(char* buffer) {
    char* tokens = strtok(buffer, " ");
    switch (str_to_token(tokens))
    {
        case ANNOUNCE:
            announce(tokens);
            return ANNOUNCE;
        
        default:
            return UNKNOWN;
    }

}



void look() {
    // Implement your look function here
}

void getfile() {
    // Implement your getfile function here
}