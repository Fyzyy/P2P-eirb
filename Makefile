TRACKER_REPO := tracker/
PEER_REPO := peer/

all: compile_tracker compile_peer

tracker: compile_tracker
	cd $(TRACKER_REPO); make; make run

peer: compile_peer
	cd $(PEER_REPO); make run

compile_peer:
	cd $(PEER_REPO); make

compile_tracker:
	cd $(TRACKER_REPO); make

clean:
	cd $(TRACKER_REPO); make clean
	cd $(PEER_REPO); make clean