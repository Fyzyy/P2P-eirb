#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "parser.h"

/*********** UTILS ******************/

char* string_tokens[MAX_TOKEN+1] = {
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
    "error",
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

/*********** ANNOUNCE ********************/

enum tokens leech_key(response* res) {
    char* separator = " []\n"; // leech
    char* keys = strtok(NULL, separator);

    if (keys != NULL)
        printf("Peer leeching key:\n");

    while (keys != NULL) {
        printf("Leech Key: %s\n", keys);
        FileInfo* file = search_tracked_file(keys);
        if (file != NULL) {
            add_leecher_to_tracked_file(keys, res->peer->ip_address, res->peer->port);
        } else {
            res->token = ERROR;
            strcpy(res->message, "Error: Unfind key. \n");
            return ERROR;
        }
        keys = strtok(NULL,separator);
    }
    res->token = OK;
    strcpy(res->message, "ok\n");
    return OK;
}

enum tokens seed(response* res) {
    char* separator = "[]\n"; // seed
    char* file_info = strtok(NULL, separator);
    printf("file_info: %s\n", file_info);

    // Aller à la partie de la liste de fichiers
    char *file_name = strtok(file_info, " ");

    if (file_name != NULL && strcmp(file_name, "leech") != 0) {
        printf("Peer seeding files:\n");
    }

    while (file_name != NULL && strcmp(file_name, "leech") != 0) {
        char* file_size_str = strtok(NULL, " ");
        char* piece_size_str = strtok(NULL, " ");
        char* file_key = strtok(NULL, " ");

        printf("File: %s, Size: %s, Piece Size: %s, Key: %s\n", file_name, file_size_str, piece_size_str, file_key);

        if (file_size_str == NULL || piece_size_str == NULL || file_key == NULL) {
            res->token = ERROR;
            strcpy(res->message, "Erreur : Paramètres manquants.\n");
            return ERROR;
        }

        int file_size = atoi(file_size_str);
        int piece_size = atoi(piece_size_str);

        if (search_tracked_file(file_key) == NULL)
            add_tracked_file(file_name, file_size, piece_size, file_key);
        add_seeder_to_tracked_file(file_key, res->peer->ip_address, res->peer->port);    

        file_name = strtok(NULL, " "); // Move to the next file name
    }

    res->token = OK;
    strcpy(res->message, "ok\n");
    return OK;
}

enum tokens announce(response* res) {

    char* usage = "Usage : announce listen $Port seed [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …] leech [$Key3 $Key4 …]\n";
    char* tokens = strtok(NULL, " "); // listen || seed || leech
    if (tokens == NULL) {
        res->token = ERROR;
        strcpy(res->message, usage);
        return ERROR;
    }

    switch (str_to_token(tokens))
    {
    case LISTEN:
        char* port = strtok(NULL, " "); //port
        printf("peer listening %d\n", atoi(port));
        res->peer->listening_port = atoi(port);
        char* tokens = strtok(NULL, " "); // seed || leech
        if (tokens == NULL || !(strcmp(tokens, "seed") == 0 || strcmp(tokens, "leech") == 0))
            return OK;
        __attribute__((fallthrough));

    case SEED:
        enum tokens ret = seed(res);
        if (ret == OK) return ret;
        __attribute__((fallthrough));

    case LEECH:
        return leech_key(res);

    case UNKNOWN:
        res->token = UNKNOWN;
        strcpy(res->message, usage);
        return UNKNOWN;

    default:
        res->token = OK;
        strcpy(res->message, "ok\n");
        return OK;
    }
}

/*********** LOOK ********************/

enum tokens look(response* res) {
    char temp_copy[256];
    char* tokens = strtok(NULL, "[");

    printf("Peer looking for files:\n");

    char* filename = strstr(tokens, "filename=");
    if (filename == NULL) {
        printf("Erreur : Clé de fichier manquante.\n");
        res->token = ERROR;
        strcpy(res->message, "Erreur : Clé de fichier manquante.\n");
        return UNKNOWN;
    }
    else {
        filename += strlen("filename=");
        filename = strtok(strncpy(temp_copy, filename, strlen(filename)), "\"");
        printf("Matching files for filename '%s':\n", filename);
        search_tracked_file(filename);
    }

    char* filesize = strstr(tokens, "filesize>");
    if (filesize != NULL) {
        filesize += strlen("filesize>");
        filesize = strtok(strncpy(temp_copy, filesize, strlen(filesize)), "\"");
        printf("Matching files for filesize > %d:\n", atoi(filesize));
    }
    
    //TODO
    return LIST;
}

/*********** GETFILE ********************/

enum tokens getfile(response* res) {
    char* key = strtok(NULL, " "); // file key
    if (key == NULL) {
        strcpy(res->message, "Usage: getfile $key\n");
        return ERROR;
    }
    printf("Peer requesting file with key: %s\n", key);

    FileInfo* file =  search_tracked_file(key);
    if (file == NULL) {
        strcpy(res->message, "Erreur : Fichier non trouvé.\n");
        res->token = ERROR;
        return ERROR;
    }
    else {
        res->token = PEERS;
        char message[MAX_BUFFER_SIZE];
        sprintf(message, "peers %s %s\n", file->key, PeersList_to_string(file->seeder));
        strcpy(res->message, message);
    }

    return PEERS;
}

/*********** UPDATE ********************/

enum tokens seed_key(response* res) {


    // Aller à la partie de la liste de keys
    char* keys = strtok(NULL, " []\r\n");

    if (keys != NULL && strcmp(keys, "leech") != 0) {
        printf("Peer seeding key:\n");
    }

    while (keys != NULL && strcmp(keys, "leech") != 0) {
        printf("Seed Key: %s\n", keys);
        keys = strtok(NULL, " ]");  
        FileInfo* file = search_tracked_file(keys);
        if (file != NULL)
        {
            add_seeder_to_tracked_file(keys, res->peer->ip_address, res->peer->port);
        }
        else if (file == NULL)
        {
            res->token = ERROR;
            strcpy(res->message, "Erreur : Fichier non trouvé.\n");
            return ERROR;
        }  
    }

    if (keys != NULL && strcmp(keys, "leech") == 0) {
        return LEECH;
    }

    res->token = OK;
    strcpy(res->message, "ok\n");
    return OK;
}

enum tokens update(response* res) {
    char* tokens = strtok(NULL, " "); // seed || leech
    switch (str_to_token(tokens)) {
        case SEED:
            if (seed_key(res) != LEECH)
                return OK;
            __attribute__((fallthrough));

        case LEECH:
            return leech_key(res);

        case UNKNOWN:
            printf("Unknown command\n");
            return UNKNOWN;

        default:
            return OK;
    }
}

/*********** PARSING ********************/

enum tokens parsing(char* buffer, response* res) {
    char* tokens = strtok(buffer, " "); // announce || look || getfile
    switch (str_to_token(tokens))
    {
        case ANNOUNCE:
            return announce(res);
        
        case LOOK :
            return look(res);
        
        case GETFILE:
            return getfile(res);
        
        case UPDATE:
            return update(res);

        case UNKNOWN:
            res->token = UNKNOWN;
            strcpy(res->message, "Commande inconnue.\n");
            return UNKNOWN;
        default:
            return UNKNOWN;
    }
}