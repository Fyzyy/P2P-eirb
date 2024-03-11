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

enum tokens parsing(char* buffer);