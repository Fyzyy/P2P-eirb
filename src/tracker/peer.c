#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "peer.h"


void display_peer_info() {
    for (int i = 0; i < numConnectedPeers; ++i) {
        printf("Peer %d:\n", i + 1);
        printf("  IP Address: %s\n", connectedPeers[i].ip_address);
        printf("  Port: %d\n", connectedPeers[i].port);
        printf("\n");
    }
}
