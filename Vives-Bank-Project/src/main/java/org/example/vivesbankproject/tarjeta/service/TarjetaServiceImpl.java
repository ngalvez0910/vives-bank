package org.example.vivesbankproject.tarjeta.service;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponseCVV;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = {"tarjetas"})
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final TarjetaMapper tarjetaMapper;

    @Autowired
    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, TarjetaMapper tarjetaMapper) {
        this.tarjetaRepository = tarjetaRepository;
        this.tarjetaMapper = tarjetaMapper;
    }

    @Override
    public Page<Tarjeta> getAll(Optional<String> numero, Optional<LocalDate> caducidad, Optional<TipoTarjeta> tipoTarjeta, Optional<BigDecimal> minLimiteDiario, Optional<BigDecimal> maxLimiteDiario, Optional<BigDecimal> minLimiteSemanal, Optional<BigDecimal> maxLimiteSemanal, Optional<BigDecimal> minLimiteMensual, Optional<BigDecimal> maxLimiteMensual, Pageable pageable) {

        Specification<Tarjeta> specNumero = (root, query, criteriaBuilder) ->
                numero.map(value -> criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroTarjeta")), "%" + value.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specCaducidad = (root, query, criteriaBuilder) ->
                caducidad.map(value -> criteriaBuilder.equal(root.get("fechaCaducidad"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specTipoTarjeta = (root, query, criteriaBuilder) ->
                tipoTarjeta.map(value -> criteriaBuilder.equal(root.get("tipoTarjeta"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMinLimiteDiario = (root, query, criteriaBuilder) ->
                minLimiteDiario.map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("limiteDiario"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMaxLimiteDiario = (root, query, criteriaBuilder) ->
                maxLimiteDiario.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("limiteDiario"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMinLimiteSemanal = (root, query, criteriaBuilder) ->
                minLimiteSemanal.map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("limiteSemanal"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMaxLimiteSemanal = (root, query, criteriaBuilder) ->
                maxLimiteSemanal.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("limiteSemanal"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMinLimiteMensual = (root, query, criteriaBuilder) ->
                minLimiteMensual.map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("limiteMensual"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMaxLimiteMensual = (root, query, criteriaBuilder) ->
                maxLimiteMensual.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("limiteMensual"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> criteria = Specification.where(specNumero)
                .and(specCaducidad)
                .and(specTipoTarjeta)
                .and(specMinLimiteDiario)
                .and(specMaxLimiteDiario)
                .and(specMinLimiteSemanal)
                .and(specMaxLimiteSemanal)
                .and(specMinLimiteMensual)
                .and(specMaxLimiteMensual);

        return tarjetaRepository.findAll(criteria, pageable);
    }


    @Override
    public TarjetaResponse getById(String id) {
        log.info("Obteniendo la tarjeta con ID: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    @Override
    public TarjetaResponseCVV getCVV(String id) {
        log.info("Obteniendo CVV de la tarjeta con ID: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        return tarjetaMapper.toTarjetaResponseCVV(tarjeta);
    }

    @Override
    public TarjetaResponse save(TarjetaRequest tarjetaRequest) {
        log.info("Guardando tarjeta: {}", tarjetaRequest);
        var tarjeta = tarjetaRepository.save(tarjetaMapper.toTarjeta(tarjetaRequest));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    @Override
    public TarjetaResponse update(String id, TarjetaRequest tarjetaRequest) {
        log.info("Actualizando tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(
                () -> new TarjetaNotFound(id)
        );
        var tarjetaUpdated = tarjetaRepository.save(tarjetaMapper.toTarjetaUpdate(tarjetaRequest, tarjeta));
        return tarjetaMapper.toTarjetaResponse(tarjetaUpdated);
    }

    @Override
    public TarjetaResponse deleteById(String id) {
        log.info("Eliminando tarjeta con ID: {}", id);
        var tarjetaExistente = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        tarjetaRepository.deleteById(tarjetaExistente.getId());
        return tarjetaMapper.toTarjetaResponse(tarjetaExistente);
    }
}