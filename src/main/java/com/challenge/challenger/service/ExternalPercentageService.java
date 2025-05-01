package com.challenge.challenger.service;

import org.springframework.stereotype.Service;
import java.util.Random;

/**
 * Servicio que simula una llamada a un servicio externo para obtener un porcentaje.
 * Este servicio es utilizado por CalculationService para obtener el porcentaje
 * que se aplicará en los cálculos.
 */
@Service
public class ExternalPercentageService {

    private final Random random = new Random();

    /**
     * Simula una llamada a un servicio externo para obtener un porcentaje.
     * Este método tiene un 10% de probabilidad de fallar para simular condiciones del mundo real.
     *
     * @return Un porcentaje aleatorio entre 5 y 20
     * @throws RuntimeException si el servicio "falla" (simulado)
     */
    public double getPercentage() {
        // Simular un 10% de probabilidad de fallo
        if (random.nextDouble() < 0.1) {
            throw new RuntimeException("Servicio externo temporalmente no disponible (FALLO SIMULADO A PROPÓSITO - Parte del diseño)");
        }

        // Devolver un porcentaje fijo de 10% (simulado)
        return 10.0;
    }
}
