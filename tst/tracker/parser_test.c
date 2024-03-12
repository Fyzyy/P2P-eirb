#include "../../src/tracker/parser.h"

#include <stdio.h>

#include <assert.h>

void test_parsing() {
    char buffer1[] = "announce [file1 100 10 1234567890 file2 200 20 0987654321]";
    enum tokens result1 = parsing(buffer1);
    assert(result1 == OK);

    char buffer2[] = "look [filename=\"foo\" filesize>\"100\"]";
    enum tokens result2 = parsing(buffer2);
    assert(result2 == LIST);

    char buffer3[] = "getfile file_key";
    enum tokens result3 = parsing(buffer3);
    assert(result3 == PEERS);

    char buffer4[] = "update seed [1 2 3] leech [4 5 6]";
    enum tokens result4 = parsing(buffer4);
    assert(result4 == OK);

    printf("All tests passed!\n");
}

int main() {
    test_parsing();
    return 0;
}
