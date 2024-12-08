package org.example.vivesbankproject.rest.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.rest.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.rest.movimientos.models.Transferencia;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientosService {
    Page<MovimientoResponse> getAll(Pageable pageable);

    MovimientoResponse getById(ObjectId _id);

    MovimientoResponse getByGuid(String guidMovimiento);

    MovimientoResponse getByClienteGuid(String idCliente);

    MovimientoResponse save(MovimientoRequest movimientoRequest);

    Domiciliacion saveDomiciliacion(User user, Domiciliacion domiciliacion);

    MovimientoResponse saveIngresoDeNomina(User user, IngresoDeNomina ingresoDeNomina);

    MovimientoResponse savePagoConTarjeta(User user, PagoConTarjeta pagoConTarjeta);

    MovimientoResponse saveTransferencia(User user, Transferencia transferencia);

    MovimientoResponse revocarTransferencia(User user, String movimientoTransferenciaGuid);

}