package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.ClienteCuentaRequest;
import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteService {

    Page<ClienteResponse> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable);

    ClienteResponse getById(String id);

    ClienteResponse save(ClienteRequestSave cliente);

    ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate);

    ClienteResponse addCuentas(String id, ClienteCuentaRequest clienteCuentaRequest);

    ClienteResponse deleteCuentas(String id, ClienteCuentaRequest clienteCuentaRequest);

    void deleteById(String id);
}