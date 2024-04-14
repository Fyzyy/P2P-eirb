# Répertoires
BUILD_DIR := build
SRC_DIR := src
PACKAGE_DIR := $(BUILD_DIR)/src

# Commandes de compilation et d'exécution
JAVAC := javac -d $(BUILD_DIR)
JAVA := java -cp $(BUILD_DIR)

# Fichiers source
SOURCES := $(wildcard $(SRC_DIR)/*.java)
CLASSES := $(SOURCES:$(SRC_DIR)/%.java=$(PACKAGE_DIR)/%.class)

# Debug
DEBUG := 0

# Port
PORT := 5050

# Cible par défaut
all: compile

# Compilation
compile: $(CLASSES)

$(PACKAGE_DIR)/%.class: $(SRC_DIR)/%.java
	@mkdir -p $(PACKAGE_DIR)
	$(JAVAC) $<

# Nettoyage
clean:
	@rm -rf $(BUILD_DIR)

# Exécution
run: compile
	$(JAVA) src.Main $(PORT) $(DEBUG)

.PHONY: all compile clean run
