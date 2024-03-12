CC = gcc
CFLAGS = -Wall -Wextra -fopenmp -I$(SRC_DIR)
SRC_DIR = src/tracker
TEST_DIR = tst/tracker
SRC = config.c parser.c
TRACKER = tracker.c
TST_SRC = $(wildcard $(TEST_DIR)/*.c)
OBJ_DIR = $(SRC_DIR)/build
OBJ = $(addprefix $(OBJ_DIR)/, $(SRC:.c=.o))
DIST_DIR = dist
EXEC = $(DIST_DIR)/tracker
TEST_EXEC = $(DIST_DIR)/test

all: $(DIST_DIR) $(OBJ_DIR) $(EXEC)

$(DIST_DIR):
	mkdir -p $(DIST_DIR)

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

$(EXEC): $(OBJ)
	$(CC) $(CFLAGS) $(SRC_DIR)/$(TRACKER) -o $(EXEC) $(OBJ)

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

$(TEST_EXEC): $(TST_SRC) $(OBJ)
	$(CC) $(CFLAGS) -o $(TEST_EXEC) $(TST_SRC) $(OBJ)

test: $(TEST_EXEC)
	./$(TEST_EXEC)

clean:
	rm -rf $(OBJ_DIR) $(DIST_DIR)

run: $(EXEC)
	./$(EXEC)

check_port:
	@if lsof -Pi :$(PORT) -sTCP:LISTEN -t >/dev/null 2>&1; then \
		echo "Port $(PORT) is in use."; \
	else \
		echo "Port $(PORT) is not in use."; \
	fi

.PHONY: all clean run check_port test
