#ifndef FILES_TEST_H
#define FILES_TEST_H

// Fonction de test pour ajouter un fichier à la liste des fichiers suivis
void test_add_tracked_file();

// Fonction de test pour supprimer un fichier de la liste des fichiers suivis
void test_remove_tracked_file();

// Fonction de test pour ajouter un pair seeder à un fichier suivi
void test_add_seeder_to_tracked_file();

// Fonction de test pour ajouter un pair leecher à un fichier suivi
void test_add_leecher_to_tracked_file();

// Fonction de test pour supprimer un pair seeder d'un fichier suivi
void test_remove_seeder_from_tracked_file();

// Fonction de test pour supprimer un pair leecher d'un fichier suivi
void test_remove_leecher_from_tracked_file();

void all_tests_files();

#endif /* FILES_TEST_H */
