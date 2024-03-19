CC = gcc
CFLAGS = -Wall -Wextra -fopenmp -pthread -I$(SRC_DIR) -g
SRC_DIR = src/tracker
TEST_DIR = tst/tracker
SRC = config.c parser.c peer.c files.c response.c
TRACKER = tracker.c
TST_SRC = parser_test.c peer_test.c files_test.c
TRACKER_TEST = tracker_test.c
OBJ_DIR = $(SRC_DIR)/build
TST_OBJ_DIR = $(TEST_DIR)/build
OBJ = $(addprefix $(OBJ_DIR)/, $(SRC:.c=.o))
TST_OBJ = $(addprefix $(TST_OBJ_DIR)/, $(TST_SRC:.c=.o))
DIST_DIR = dist
EXEC = $(DIST_DIR)/tracker
TEST_EXEC = $(DIST_DIR)/test

all: $(DIST_DIR) $(OBJ_DIR) $(TST_OBJ_DIR) $(EXEC) $(TEST_EXEC)

$(DIST_DIR) $(OBJ_DIR) $(TST_OBJ_DIR):
	mkdir -p $@

$(EXEC): $(OBJ)
	$(CC) $(CFLAGS) $(SRC_DIR)/$(TRACKER) $^ -o $@

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

$(TEST_EXEC): $(TST_OBJ) $(OBJ)
	$(CC) $(CFLAGS) $(TEST_DIR)/$(TRACKER_TEST) $^ -o $@

$(TST_OBJ_DIR)/%.o: $(TEST_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

test: $(TEST_EXEC)
	./$(TEST_EXEC)

clean:
	rm -rf $(OBJ_DIR) $(TST_OBJ_DIR) $(DIST_DIR)

run: $(EXEC)
	./$(EXEC)

debug_test: $(TEST_EXEC)
	gdb ./$(TEST_EXEC)

valgrind: $(EXEC)
	valgrind ./$(EXEC)

valgrind_test: $(TEST_EXEC)
	valgrind ./$(TEST_EXEC)

check_port:
	@if lsof -Pi :$(PORT) -sTCP:LISTEN -t >/dev/null 2>&1; then \
		echo "Port $(PORT) is in use."; \
	else \
		echo "Port $(PORT) is not in use."; \
	fi

.PHONY: all clean run check_port test
