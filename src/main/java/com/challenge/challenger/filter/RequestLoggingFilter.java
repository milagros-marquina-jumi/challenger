package com.challenge.challenger.filter;

import com.challenge.challenger.service.CallHistoryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * Filtro para registrar todas las llamadas a la API.
 * Captura la información de la solicitud y la respuesta, y la guarda
 * de forma asíncrona en la base de datos a través del CallHistoryService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final CallHistoryService callHistoryService;

    /**
     * Método principal del filtro que intercepta todas las solicitudes HTTP.
     * Registra las llamadas a la API y omite las solicitudes que no son de la API.
     *
     * @param request La solicitud HTTP
     * @param response La respuesta HTTP
     * @param filterChain La cadena de filtros
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        // Omitir registro para solicitudes que no son de la API
        if (!request.getRequestURI().startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Envolver la solicitud y respuesta para poder leer sus contenidos
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // Continuar con la cadena de filtros
            filterChain.doFilter(requestWrapper, responseWrapper);

            // Registrar la solicitud y respuesta
            String endpoint = request.getRequestURI();
            String parameters = getRequestBody(requestWrapper);
            String responseBody = ""; // Omitir cuerpo de respuesta para evitar problemas de serialización
            boolean successful = response.getStatus() < 400;
            String errorMessage = successful ? null : "Estado HTTP: " + response.getStatus();

            // Registrar la llamada de forma asíncrona
            callHistoryService.logCall(endpoint, parameters, responseBody, errorMessage, successful);

        } finally {
            // Copiar el contenido de vuelta a la respuesta
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * Obtiene el cuerpo de la solicitud como una cadena de texto.
     *
     * @param request La solicitud HTTP envuelta
     * @return El cuerpo de la solicitud como cadena de texto, o cadena vacía si no hay cuerpo
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            try {
                return new String(content, request.getCharacterEncoding());
            } catch (IOException e) {
                log.error("Error al leer el cuerpo de la solicitud", e);
                return "Error al leer el cuerpo de la solicitud: " + e.getMessage();
            }
        }
        return "";
    }
}
