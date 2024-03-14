#ifndef RESPONSE_H
#define RESPONSE_H

#include "peer.h"

enum tokens {
    ANNOUNCE,
    LISTEN,
    SEED,
    LEECH,
    LOOK,
    GETFILE,
    LIST,
    UPDATE,
    PEERS,
    OK,
    UNKNOWN,
    ERROR,
    MAX_TOKEN,    
};


typedef struct response
{
    enum tokens token;
    char message[MAX_BUFFER_SIZE];
    PeerInfo* peer;

}response;


response* create_response( PeerInfo* peer);

void print_response(response* res);



#endif // RESPONSE_H