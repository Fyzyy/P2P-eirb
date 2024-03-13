#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>

#include "parser_test.h"
#include "../../src/tracker/parser.h"

#include <assert.h>

void test_parsing() {
    puts("Testing 1\n");
    char buffer1[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321] leech [1221 212121]";
    enum tokens result1 = parsing(buffer1);
    assert(result1 == OK);

    puts("Testing 2\n");
    char buffer2[] = "look [filename=\"foo\" filesize>\"100\"]";
    enum tokens result2 = parsing(buffer2);
    assert(result2 == LIST);

    puts("Testing 3\n");
    char buffer3[] = "getfile file_key";
    enum tokens result3 = parsing(buffer3);
    assert(result3 == PEERS);

    puts("Testing 4\n");
    char buffer4[] = "update seed [1 2 3] leech [4 5 6]";
    enum tokens result4 = parsing(buffer4);
    assert(result4 == OK);

    printf("All tests passed!\n");
}

void test_limit_cases() {

    // Test empty buffer
    char buffer2[] = "okkk";
    enum tokens result2 = parsing(buffer2);
    assert(result2 == UNKNOWN);

    // Test buffer with only whitespace
    char buffer3[] = "   \t\n";
    enum tokens result3 = parsing(buffer3);
    assert(result3 == UNKNOWN);

    // Test buffer with invalid syntax
    char buffer4[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321";
    enum tokens result4 = parsing(buffer4);
    assert(result4 == OK);

    printf("All limit cases passed!\n");
}

void all_tests_parser() {
    puts("Testing parser functions...\n");
    test_parsing();
    test_limit_cases();
    printf("All parser tests passed successfully!\n");
}
