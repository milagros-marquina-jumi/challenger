package com.challenge.challenger.controller;

import com.challenge.challenger.model.CalculationRequest;
import com.challenge.challenger.model.CalculationResponse;
import com.challenge.challenger.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controlador para realizar cálculos con porcentaje dinámico.
 * Proporciona endpoints para sumar dos números y aplicar un porcentaje obtenido de un servicio externo.
 *
 * Este controlador implementa rate limiting para prevenir el abuso de la API.
 * El límite es de 5 solicitudes por ventana de 10 segundos.
 */
@RestController
@RequestMapping("/api/calculations")
@RequiredArgsConstructor
@Slf4j
public class CalculationController {

    private final CalculationService calculationService;

    // Rate limiting: 5 solicitudes por ventana de 10 segundos
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicLong windowStartTime = new AtomicLong(System.currentTimeMillis());
    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SIZE_MS = 10000; // 10 segundos

    /**
     * Realiza un cálculo sumando dos números y aplicando un porcentaje dinámico.
     * Implementa rate limiting para prevenir el abuso de la API.
     *
     * @param request Objeto con los números a sumar
     * @return Respuesta con el resultado del cálculo o error 429 si se excede el límite
     */
    @PostMapping
    public ResponseEntity<?> calculate(@RequestBody CalculationRequest request) {
        // Comprobar rate limit
        if (!checkRateLimit()) {
            log.warn("Rate limit excedido: {} solicitudes en la ventana actual", requestCount.get());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            errorResponse.put("error", "Too Many Requests");
            errorResponse.put("message", "Has excedido el límite de solicitudes. Por favor, intenta de nuevo más tarde.");

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }

        // Procesar la solicitud normalmente
        CalculationResponse response = calculationService.calculate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Comprueba si la solicitud actual excede el límite de tasa.
     *
     * @return true si la solicitud está dentro del límite, false si excede el límite
     */
    private boolean checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        long windowStart = windowStartTime.get();

        // Si ha pasado el tiempo de la ventana, reiniciar el contador
        if (currentTime - windowStart > WINDOW_SIZE_MS) {
            windowStartTime.set(currentTime);
            requestCount.set(1); // Esta es la primera solicitud en la nueva ventana
            return true;
        }

        // Incrementar el contador y comprobar si excede el límite
        return requestCount.incrementAndGet() <= MAX_REQUESTS;
    }
}
