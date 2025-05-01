package com.challenge.challenger.repository;

import com.challenge.challenger.entity.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repositorio para acceder a los datos de historial de llamadas.
 * Proporciona métodos para buscar y filtrar el historial.
 */
@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {

    /**
     * Encuentra todas las entradas de historial ordenadas por fecha descendente.
     *
     * @param pageable Información de paginación
     * @return Página de entradas de historial
     */
    Page<CallHistory> findAllByOrderByTimestampDesc(Pageable pageable);

    /**
     * Encuentra entradas de historial que contengan el endpoint especificado.
     *
     * @param endpoint El endpoint a buscar
     * @param pageable Información de paginación
     * @return Página de entradas de historial filtradas por endpoint
     */
    Page<CallHistory> findByEndpointContainingOrderByTimestampDesc(String endpoint, Pageable pageable);

    /**
     * Encuentra entradas de historial entre dos fechas.
     *
     * @param start Fecha de inicio
     * @param end Fecha de fin
     * @param pageable Información de paginación
     * @return Página de entradas de historial filtradas por rango de fechas
     */
    Page<CallHistory> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
