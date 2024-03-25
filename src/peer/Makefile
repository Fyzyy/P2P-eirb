COMPIL := javac -d build
EXEC := java -cp
BUILD_REPO := build
SRC_REPO := .
TEST_REPO := tst
PACKAGE_REPO := build/peer

all: compile

compile:
	$(COMPIL) $(SRC_REPO)/*.java

run: compile
	$(EXEC) $(BUILD_REPO) Main

clean:
	rm -f $(BUILD_REPO)/*.class
	rm -f $(PACKAGE_REPO)/*.class