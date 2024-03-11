COMPIL := javac -d build
EXEC := java -ea -cp
BUILD_REPO := build
SRC_REPO := src
TEST_REPO := tst

all: exec

compile:
	$(COMPIL) $(SRC_REPO)/*.java

exec: compile
	$(EXEC) $(BUILD_REPO) Peer

clean:
	rm -f $(BUILD_REPO)/*.class