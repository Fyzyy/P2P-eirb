#Tracker

CC = gcc
CFLAGS = -Wall -Wextra -fopenmp -I$(SRC_DIR)
SRC_DIR = src/tracker
TEST_DIR = tst/tracker
SRC = config.c tracker.c parser.c
OBJ_DIR = $(SRC_DIR)/build
OBJ = $(addprefix $(OBJ_DIR)/, $(SRC:.c=.o))
DIST_DIR = dist
EXEC = $(DIST_DIR)/tracker

all: $(DIST_DIR) $(OBJ_DIR) $(EXEC)

$(DIST_DIR):
	mkdir -p $(DIST_DIR)

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

$(EXEC): $(OBJ)
	$(CC) $(CFLAGS) -o  $(EXEC) $(OBJ)

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf $(OBJ_DIR) $(DIST_DIR) $(EXEC)

run: $(EXEC)
	./$(EXEC)

check_port:
	@if lsof -Pi :$(PORT) -sTCP:LISTEN -t >/dev/null 2>&1; then \
		echo "Port $(PORT) is in use."; \
	else \
		echo "Port $(PORT) is not in use."; \
	fi

.PHONY: all clean run check_port