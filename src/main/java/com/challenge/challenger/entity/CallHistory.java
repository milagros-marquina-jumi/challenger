package com.challenge.challenger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una entrada en el historial de llamadas a la API.
 * Almacena información sobre la solicitud, la respuesta y el resultado de la llamada.
 */
@Entity
@Table(name = "call_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallHistory {

    /**
     * Identificador único de la entrada en el historial.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * URL del endpoint que fue llamado.
     */
    @Column(nullable = false)
    private String endpoint;

    /**
     * Fecha y hora en que se realizó la llamada.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Parámetros de la llamada en formato JSON.
     */
    @Column(nullable = false, length = 1000)
    private String parameters;

    /**
     * Respuesta de la llamada en formato JSON.
     */
    @Column(length = 2000)
    private String response;

    /**
     * Mensaje de error si la llamada falló.
     */
    @Column(length = 1000)
    private String errorMessage;

    /**
     * Indica si la llamada fue exitosa (true) o falló (false).
     */
    @Column(nullable = false)
    private boolean successful;
}
