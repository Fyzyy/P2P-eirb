COMPIL := javac -d build
EXEC := java -cp
BUILD_REPO := build
SRC_REPO := .
TEST_REPO := tst

all: exec

compile:
	$(COMPIL) $(SRC_REPO)/*.java

exec: compile
	$(EXEC) $(BUILD_REPO) Main

clean:
	rm -f $(BUILD_REPO)/*.class