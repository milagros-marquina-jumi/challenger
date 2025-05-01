package com.challenge.challenger.service;

import com.challenge.challenger.exception.ExternalServiceException;
import com.challenge.challenger.model.CalculationRequest;
import com.challenge.challenger.model.CalculationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para realizar cálculos con porcentaje dinámico.
 * Implementa un mecanismo de caché para almacenar el porcentaje durante 30 minutos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationService {

    private final ExternalPercentageService externalPercentageService;

    // Valores para la caché del porcentaje
    private Double cachedPercentage = null;
    private LocalDateTime cacheTime = null;

    /**
     * Calcula el resultado sumando num1 y num2 y aplicando un porcentaje dinámico.
     *
     * @param request La solicitud de cálculo que contiene num1 y num2
     * @return La respuesta del cálculo con el resultado
     */
    public CalculationResponse calculate(CalculationRequest request) {
        double sum = request.num1() + request.num2();
        double percentage = getPercentage();
        double result = sum + (sum * percentage / 100);

        return new CalculationResponse(
            request.num1(),
            request.num2(),
            sum,
            percentage,
            result,
            LocalDateTime.now()
        );
    }

    /**
     * Obtiene el porcentaje del servicio externo o de la caché.
     * Si el servicio externo falla y hay un valor en caché, se usa ese valor
     * incluso si ha expirado.
     *
     * @return El porcentaje a aplicar
     * @throws ExternalServiceException si no se puede obtener el porcentaje y no hay valor en caché
     */
    public double getPercentage() {
        // Verificar si tenemos un valor en caché que tenga menos de 30 minutos
        if (cachedPercentage != null && cacheTime != null &&
            cacheTime.plusMinutes(30).isAfter(LocalDateTime.now())) {
            log.info("Usando porcentaje en caché: {}", cachedPercentage);
            return cachedPercentage;
        }

        try {
            // Intentar obtener el porcentaje del servicio externo
            double percentage = externalPercentageService.getPercentage();
            log.info("Porcentaje obtenido del servicio externo: {}", percentage);

            // Guardar el valor en caché
            cachedPercentage = percentage;
            cacheTime = LocalDateTime.now();

            return percentage;
        } catch (Exception e) {
            log.error("Error al obtener el porcentaje del servicio externo", e);

            // Si tenemos un valor en caché, usarlo aunque haya expirado
            if (cachedPercentage != null) {
                log.info("Usando porcentaje en caché expirado: {}", cachedPercentage);
                return cachedPercentage;
            }

            // Si no hay valor en caché, lanzar excepción
            throw new ExternalServiceException("Error al obtener el porcentaje del servicio externo (FALLO SIMULADO A PROPÓSITO - Parte del diseño)", e);
        }
    }
}
