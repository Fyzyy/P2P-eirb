#define MAX_LINE_LENGTH 256
#define MAX_VALUE_LENGTH 256

struct ServerConfig {
    int port;
    char address[MAX_VALUE_LENGTH];
};

void load_config(const char *file_path, struct ServerConfig *config);