#ifndef PARSER_H
#define PARSER_H

#include "response.h"
#include "files.h"

enum tokens parsing(char* buffer, response* res);

#endif /* PARSER_H */