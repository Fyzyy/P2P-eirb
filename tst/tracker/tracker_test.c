#include "parser_test.h"
#include "peer_test.h"
#include "files_test.h"
#include <stdio.h>

#define YELLOW_TEXT(text) "\033[0;33m" text "\033[0m"
#define GREEN_TEXT(text) "\033[0;32m" text "\033[0m"




int main() {
    printf(YELLOW_TEXT("Running tests...\n"));

    init_global_lists();

    all_tests_parser();
    // all_tests_peer();
    // all_tests_files();

    remove_list(allPeers);
    remove_list(connectedPeers);

    printf(GREEN_TEXT("All tests passed successfully!\n"));

    return 0;
}