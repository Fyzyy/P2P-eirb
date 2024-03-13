#include "parser_test.h"
#include "peer_test.h"
#include <stdio.h>




int main() {
    printf("Running tests...\n");

    all_tests_parser();
    all_tests_peer();   

    printf("All tests passed successfully!\n");

    return 0;
}