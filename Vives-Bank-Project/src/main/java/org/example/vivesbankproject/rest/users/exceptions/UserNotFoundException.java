package org.example.vivesbankproject.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends UserException  {
    public UserNotFoundException(String username) {
        super("Usuario con username '" + username + "' no encontrado");
    }
}