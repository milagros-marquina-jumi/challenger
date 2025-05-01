package com.challenge.challenger.controller;

import com.challenge.challenger.entity.CallHistory;
import com.challenge.challenger.service.CallHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class CallHistoryController {

    private final CallHistoryService callHistoryService;

    /**
     * Obtiene el historial de llamadas a la API con filtros opcionales.
     *
     * @param endpoint Filtrar por endpoint (opcional)
     * @param startDate Fecha de inicio para filtrar (formato: YYYY-MM-DD) (opcional)
     * @param endDate Fecha de fin para filtrar (formato: YYYY-MM-DD) (opcional)
     * @param page Número de página (desde 0)
     * @param size Tamaño de página
     * @return Página con el historial de llamadas
     */
    @GetMapping
    public ResponseEntity<Page<CallHistory>> getCallHistory(
        @RequestParam(required = false) String endpoint,

        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        @RequestParam(defaultValue = "0") int page,

        @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<CallHistory> history;

        if (endpoint != null) {
            history = callHistoryService.getCallHistoryByEndpoint(endpoint, pageRequest);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            history = callHistoryService.getCallHistoryByTimeRange(startDateTime, endDateTime, pageRequest);
        } else {
            history = callHistoryService.getCallHistory(pageRequest);
        }

        return ResponseEntity.ok(history);
    }

    /**
     * Versión simplificada del historial que devuelve una lista en lugar de una página paginada.
     * Ideal para pruebas rápidas y visualización simple.
     *
     * @param endpoint Filtrar por endpoint (opcional)
     * @param limit Número máximo de resultados a devolver
     * @return Lista con el historial de llamadas (limitada al número especificado)
     */
    @GetMapping("/simple")
    public ResponseEntity<List<CallHistory>> getSimpleHistory(
        @RequestParam(required = false) String endpoint,

        @RequestParam(defaultValue = "10") int limit
    ) {
        List<CallHistory> history;

        if (endpoint != null) {
            history = callHistoryService.getRecentCallHistoryByEndpoint(endpoint, limit);
        } else {
            history = callHistoryService.getRecentCallHistory(limit);
        }

        return ResponseEntity.ok(history);
    }
}
