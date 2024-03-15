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

void test_announce() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321] leech [1221 212121]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == OK);

    remove_peer(peer);
    free(res);
}

void test_look() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "look [filename=\"foo\" filesize>\"100\"]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == LIST);

    remove_peer(peer);
    free(res);
}

void test_getfile() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    display_peers(allPeers);

    char buffer[] = "getfile file_key\n";
    enum tokens result = parsing(buffer, res);
    assert(result == ERROR);

    remove_peer(peer);
    free(res);
}

void test_update() {
    PeerInfo* peer = new_peer(NULL,"192.168.0.1",2222);
    response* res = create_response(peer);

    char buffer[] = "update seed [1 2 3] leech [4 5 6]\n";
    enum tokens result = parsing(buffer, res);
    assert(result == OK);

    remove_peer(res->peer);
    free(res);
}

void all_tests_parser() {
    puts(YELLOW_TEXT("Testing parser functions..."));
    // test_announce();
    // test_look();
    test_getfile();
    // test_update();
    printf(GREEN_TEXT("All tests on parser passed successfully!\n\n"));
}
