TRACKER_REPO := tracker/
PEER_REPO := peer/
APP_REPO := app/

all: compile_tracker compile_peer

tracker: compile_tracker
	cd $(TRACKER_REPO); make; make run

peer: compile_peer
	cd $(PEER_REPO); make run

compile_peer:
	cd $(PEER_REPO); make

compile_tracker:
	cd $(TRACKER_REPO); make

app:
	cd $(APP_REPO); pnpm install; pnpm run dev

clean:
	cd $(TRACKER_REPO); make clean
	cd $(PEER_REPO); make clean