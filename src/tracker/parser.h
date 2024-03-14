#ifndef PARSER_H
#define PARSER_H

#include "files.h"

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
    MAX_TOKEN,    
};


struct response
{
    enum tokens token;
    void* data;
    void* peers;
};



enum tokens parsing(char* buffer, const char* ip, int port);

#endif /* PARSER_H */