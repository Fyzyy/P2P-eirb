CC = gcc
CFLAGS = -Wall -Wextra
SRC = config.c tracker.c
OBJ_DIR = build
OBJ = $(addprefix $(OBJ_DIR)/, $(SRC:.c=.o))
EXEC = tracker

all: $(OBJ_DIR) $(EXEC)

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

$(EXEC): $(OBJ)
	$(CC) $(CFLAGS) -o $(EXEC) $(OBJ)

$(OBJ_DIR)/%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf $(OBJ_DIR) $(EXEC)

run: $(EXEC)
	./$(EXEC)

check_port:
	@if lsof -Pi :$(PORT) -sTCP:LISTEN -t >/dev/null 2>&1; then \
		echo "Port $(PORT) is in use."; \
	else \
		echo "Port $(PORT) is not in use."; \
	fi

.PHONY: all clean run check_port
