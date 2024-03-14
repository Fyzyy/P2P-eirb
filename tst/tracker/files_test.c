#include "../../src/tracker/files.h"
#include <assert.h>


#define YELLOW_TEXT(text) "\033[0;33m" text "\033[0m"
#define GREEN_TEXT(text) "\033[0;32m" text "\033[0m"
#define RED_TEXT(text) "\033[0;31m" text "\033[0m"

// Fonction de test pour ajouter un fichier à la liste des fichiers suivis
void test_add_tracked_file() {
    reset_tracked_files();
    add_tracked_file("file1.txt", 1000, 256, "key1");
    assert(trackedFiles != NULL);
    assert(strcmp(trackedFiles->filename, "file1.txt") == 0);
    assert(trackedFiles->length == 1000);
    assert(trackedFiles->pieceSize == 256);
    assert(strcmp(trackedFiles->key, "key1") == 0);
    assert(trackedFiles->seeder->n_peers == 0);
    assert(trackedFiles->leecher->n_peers == 0);
}

// Fonction de test pour supprimer un fichier de la liste des fichiers suivis
void test_remove_tracked_file() {
    reset_tracked_files();
    // Ajouter un fichier pour le supprimer ensuite
    add_tracked_file("file2.txt", 2000, 512, "key2");
    assert(trackedFiles != NULL);
    remove_tracked_file("key2");
    assert(trackedFiles == NULL);
}

// Fonction de test pour ajouter un pair seeder à un fichier suivi
void test_add_seeder_to_tracked_file() {
    reset_tracked_files();
    // Ajouter un fichier
    add_tracked_file("file3.txt", 3000, 512, "key3");
    assert(trackedFiles != NULL);
    // Ajouter un seeder
    add_seeder_to_tracked_file("key3", "192.168.1.1", 8080);
    assert(trackedFiles->seeder->n_peers == 1);
}

// Fonction de test pour ajouter un pair leecher à un fichier suivi
void test_add_leecher_to_tracked_file() {
    reset_tracked_files();
    // Ajouter un fichier
    add_tracked_file("file4.txt", 4000, 512, "key4");
    assert(trackedFiles != NULL);
    // Ajouter un leecher
    add_leecher_to_tracked_file("key4", "192.168.1.2", 8081);
    assert(trackedFiles->leecher->n_peers == 1);
}

// Fonction de test pour supprimer un pair seeder d'un fichier suivi
void test_remove_seeder_from_tracked_file() {
    reset_tracked_files();
    // Ajouter un fichier avec un seeder
    add_tracked_file("file5.txt", 5000, 512, "key5");
    assert(trackedFiles != NULL);
    add_seeder_to_tracked_file("key5", "192.168.1.3", 8082);
    assert(trackedFiles->seeder->n_peers == 1);
    // Supprimer le seeder
    remove_seeder_from_tracked_file("key5", "192.168.1.3", 8082);
    assert(trackedFiles->seeder->n_peers == 0);
}

// Fonction de test pour supprimer un pair leecher d'un fichier suivi
void test_remove_leecher_from_tracked_file() {
    reset_tracked_files();
    // Ajouter un fichier avec un leecher
    add_tracked_file("file6.txt", 6000, 512, "key6");
    assert(trackedFiles != NULL);
    add_leecher_to_tracked_file("key6", "192.168.1.4", 8083);
    assert(trackedFiles->leecher->n_peers == 1);
    // Supprimer le leecher
    remove_leecher_from_tracked_file("key6", "192.168.1.4", 8083);
    assert(trackedFiles->leecher->n_peers == 0);
}

void all_tests_files() {
    puts(YELLOW_TEXT("Testing files functions..."));
    test_add_tracked_file();
    test_remove_tracked_file();
    test_add_seeder_to_tracked_file();
    test_add_leecher_to_tracked_file();
    test_remove_seeder_from_tracked_file();
    test_remove_leecher_from_tracked_file();
    puts(GREEN_TEXT("All tests on files passed successfully!\n"));

}
