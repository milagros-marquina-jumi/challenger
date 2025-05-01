package com.challenge.challenger.service;

import com.challenge.challenger.entity.CallHistory;
import com.challenge.challenger.repository.CallHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para gestionar el historial de llamadas a la API.
 * Proporciona métodos para registrar llamadas de forma asíncrona y
 * consultar el historial con diferentes filtros.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CallHistoryService {

    private final CallHistoryRepository callHistoryRepository;

    /**
     * Registra una llamada a la API de forma asíncrona.
     *
     * @param endpoint     El endpoint que fue llamado
     * @param parameters   Los parámetros de la llamada
     * @param response     La respuesta de la llamada
     * @param errorMessage El mensaje de error si la llamada falló
     * @param successful   Si la llamada fue exitosa o no
     * @return Un CompletableFuture que se completará cuando la llamada sea registrada
     */
    @Async
    public CompletableFuture<CallHistory> logCall(String endpoint, String parameters, String response, String errorMessage, boolean successful) {
        CallHistory callHistory = CallHistory.builder()
            .endpoint(endpoint)
            .timestamp(LocalDateTime.now())
            .parameters(parameters)
            .response(response)
            .errorMessage(errorMessage)
            .successful(successful)
            .build();

        log.info("Registrando llamada a {}: {}", endpoint, parameters);
        return CompletableFuture.completedFuture(callHistoryRepository.save(callHistory));
    }

    /**
     * Obtiene el historial de llamadas con paginación.
     *
     * @param pageable La información de paginación
     * @return Una página del historial de llamadas
     */
    public Page<CallHistory> getCallHistory(Pageable pageable) {
        return callHistoryRepository.findAllByOrderByTimestampDesc(pageable);
    }

    /**
     * Obtiene el historial de llamadas para un endpoint específico.
     *
     * @param endpoint El endpoint por el que filtrar
     * @param pageable La información de paginación
     * @return Una página del historial de llamadas filtrado por endpoint
     */
    public Page<CallHistory> getCallHistoryByEndpoint(String endpoint, Pageable pageable) {
        return callHistoryRepository.findByEndpointContainingOrderByTimestampDesc(endpoint, pageable);
    }

    /**
     * Obtiene el historial de llamadas para un rango de tiempo específico.
     *
     * @param start    El inicio del rango de tiempo
     * @param end      El fin del rango de tiempo
     * @param pageable La información de paginación
     * @return Una página del historial de llamadas filtrado por rango de tiempo
     */
    public Page<CallHistory> getCallHistoryByTimeRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return callHistoryRepository.findByTimestampBetweenOrderByTimestampDesc(start, end, pageable);
    }

    /**
     * Obtiene las entradas más recientes del historial de llamadas.
     * Versión simplificada sin paginación, ideal para pruebas rápidas.
     *
     * @param limit El número máximo de entradas a devolver
     * @return Una lista de entradas del historial de llamadas
     */
    public List<CallHistory> getRecentCallHistory(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        return callHistoryRepository.findAllByOrderByTimestampDesc(pageRequest).getContent();
    }

    /**
     * Obtiene las entradas más recientes del historial de llamadas para un endpoint específico.
     * Versión simplificada sin paginación, ideal para pruebas rápidas.
     *
     * @param endpoint El endpoint por el que filtrar
     * @param limit El número máximo de entradas a devolver
     * @return Una lista de entradas del historial de llamadas filtrado por endpoint
     */
    public List<CallHistory> getRecentCallHistoryByEndpoint(String endpoint, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        return callHistoryRepository.findByEndpointContainingOrderByTimestampDesc(endpoint, pageRequest).getContent();
    }
}
