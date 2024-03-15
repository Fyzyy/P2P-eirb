#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "config.h"


void load_config(const char *file_path, struct ServerConfig *config) {
    FILE *file = fopen(file_path, "r");
    if (!file) {
        fprintf(stderr, "Error opening file: %s\n", file_path);
    }

    char line[MAX_LINE_LENGTH];
    while (fgets(line, sizeof(line), file)) {
        // Supprimer les espaces en début et fin de ligne
        char *trimmed_line = strtok(line, "\r\n\t");

        // Ignorer les lignes vides et les commentaires
        if (trimmed_line == NULL || trimmed_line[0] == '#' || trimmed_line[0] == ';') {
            continue;
        }

        // Séparer la ligne en clé et valeur
        char *key = strtok(trimmed_line, " =");
        char *value = strtok(NULL, " =");

        if (key && value) {
            // Supprimer les espaces en début et fin de clé et valeur
            char *trimmed_key = strtok(key, "\t ");
            char *trimmed_value = strtok(value, "\t ");

            // Comparer les clés et stocker les valeurs correspondantes
            if (strcmp(trimmed_key, "tracker-port") == 0) {
                config->port = atoi(trimmed_value);
            } else if (strcmp(trimmed_key, "tracker-address") == 0) {
                strncpy(config->address, trimmed_value, sizeof(config->address) - 1);
            }
        }
    }

    printf("Server Configurations:\n");
    printf("Port: %d\n", config->port);
    printf("Address: %s\n", config->address);
    fclose(file);
}
