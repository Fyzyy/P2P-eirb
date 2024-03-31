

#include "response.h"


response* create_response(PeerInfo* peer) {
    response* res = (response*) malloc(sizeof(response));
    res->peer = peer;
    return res;

}


void print_response(response* res) {
    printf("Peer : %s:%d |", res->peer->ip_address, res->peer->port);
    printf("Token : %d |", res->token);
    printf("Message : %s\n", res->message);
}