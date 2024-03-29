#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>

#include "parser_test.h"
#include "../../src/tracker/parser.h"

#include <assert.h>

#define YELLOW_TEXT(text) "\033[0;33m" text "\033[0m"
#define GREEN_TEXT(text) "\033[0;32m" text "\033[0m"
#define RED_TEXT(text) "\033[0;31m" text "\033[0m"

void clean() {
    reset_tracked_files();
    remove_all_peers();
}

void test_announce() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321] leech [file1 file2]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == OK);

    clean();
    free(res);
}

void test_listen() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "announce listen 2222\n";
    enum tokens result = parsing(buffer, res);
    assert(allPeers->peers[0]->port == 2222);
    assert(result == OK);

    clean();
    free(res);
}

void test_seed() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == OK);
    assert(search_tracked_file("1234567890") != NULL);
    assert(search_tracked_file("0987654321") != NULL);
    FileInfo* file1 = search_tracked_file("1234567890");
    assert(file1->length == 100);
    assert(strcmp(file1->key, "1234567890") == 0);

    assert(file1->seeder->n_peers == 1);
    assert(file1->leecher->n_peers == 0);
    assert(strcmp(file1->seeder->peers[0]->ip_address, peer->ip_address) == 0);
    assert(allPeers->n_peers == 1);

    clean();
    free(res);
}

void test_leech() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    add_tracked_file("file1", 100, 10, "key1");
    add_tracked_file("file2", 200, 20, "key2");

    assert(search_tracked_file("key1") != NULL);

    char buffer[] = "announce leech [key1 key2]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == OK);
    assert(search_tracked_file("key1")->leecher->n_peers == 1);
    assert(search_tracked_file("key2")->leecher->n_peers == 1);
    assert(strcmp(search_tracked_file("key1")->leecher->peers[0]->ip_address, peer->ip_address) == 0);
    assert(strcmp(search_tracked_file("key2")->leecher->peers[0]->ip_address, peer->ip_address) == 0);
    assert(allPeers->n_peers == 1);


    clean();
    free(res);
}

void test_look() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    add_tracked_file("foo", 101, 1, "key1");

    char buffer[] = "look [filename=\"foo\" filesize>\"100\"]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == LIST);

    clean();
    free(res);
}

void test_getfile() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "getfile file_key\n";
    enum tokens result = parsing(buffer, res);
    assert(result == ERROR);

    clean();
    free(res);
}

void test_update() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "update seed [1 2 3] leech [4 5 6]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == OK);

    clean();
    free(res);
}

void all_tests_parser() {
    puts(YELLOW_TEXT("Testing parser functions..."));
    test_announce();
    test_listen();
    test_seed();
    test_leech();
    test_look();
    test_getfile();
    test_update();
    printf(GREEN_TEXT("All tests on parser passed successfully!\n\n"));
}
