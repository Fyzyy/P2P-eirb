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

void test_parsing() {
    char buffer1[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321] leech [1221 212121]";
    enum tokens result1 = parsing(buffer1, "0.0.0.0", 9999);
    assert(result1 == OK);

    char buffer2[] = "look [filename=\"foo\" filesize>\"100\"]";
    enum tokens result2 = parsing(buffer2, "0.0.0.0", 9999);
    assert(result2 == LIST);

    char buffer3[] = "getfile file_key";
    enum tokens result3 = parsing(buffer3, "0.0.0.0", 9999);
    assert(result3 == PEERS);

    char buffer4[] = "update seed [1 2 3] leech [4 5 6]";
    enum tokens result4 = parsing(buffer4, "0.0.0.0", 9999);
    assert(result4 == OK);

}

void test_limit_cases() {

    // Test empty buffer
    char buffer2[] = "okkk";
    enum tokens result2 = parsing(buffer2, "0.0.0.0", 9999);
    assert(result2 == UNKNOWN);

    // Test buffer with only whitespace
    char buffer3[] = "   \t\n";
    enum tokens result3 = parsing(buffer3, "0.0.0.0", 9999);
    assert(result3 == UNKNOWN);

    // Test buffer with invalid syntax
    char buffer4[] = "announce listen 2222 seed [file1 100 10 1234567890 file2 200 20 0987654321";
    enum tokens result4 = parsing(buffer4, "0.0.0.0", 9999);
    assert(result4 == OK);
}

void all_tests_parser() {
    puts(YELLOW_TEXT("Testing parser functions..."));
    test_parsing();
    test_limit_cases();
    printf(GREEN_TEXT("All tests on parser passed successfully!\n\n"));
}
