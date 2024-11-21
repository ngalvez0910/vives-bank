package org.example.vivesbankproject.movimientos.exceptions;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class MovimientoNotFound  extends MovimientosException{
    public MovimientoNotFound(String id) {
        super("El movimiento con id " + id + " no existe");
    }
}
