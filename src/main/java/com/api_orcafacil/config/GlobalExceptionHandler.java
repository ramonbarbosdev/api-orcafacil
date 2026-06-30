package com.api_orcafacil.config;

import java.time.LocalDateTime;

import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.api_orcafacil.dto.ApiErrorResponse;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ExceptionCauseResolver;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String HINT_PADRAO = "Tente novamente. Se o problema persistir, contate o suporte.";

    @Value("${app.api.expose-error-details:true}")
    private boolean exposeErrorDetails;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(404, "NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> unauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return build(401, "UNAUTHORIZED", ex.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dados invalidos");
        return build(422, "VALIDATION_ERROR", message, null, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> business(BusinessException ex, HttpServletRequest request) {
        return build(422, "BUSINESS_ERROR", ex.getMessage(), null, request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> conflict(ConflictException ex, HttpServletRequest request) {
        return build(409, "CONFLICT", ex.getMessage(), null, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violacao de integridade em {}: {}", request.getRequestURI(), ex.getMessage());
        String message = mensagemIntegridade(ex);
        return build(422, "DATA_INTEGRITY_ERROR", message, HINT_PADRAO, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> responseStatus(ResponseStatusException ex, HttpServletRequest request) {
        int status = ex.getStatusCode().value();
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        String error = switch (status) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 409 -> "CONFLICT";
            case 422 -> "BUSINESS_ERROR";
            default -> "HTTP_ERROR";
        };
        return build(status, error, message, null, request);
    }

    @ExceptionHandler({
            DataAccessException.class,
            LazyInitializationException.class,
            IllegalStateException.class,
            HttpMessageNotWritableException.class
    })
    public ResponseEntity<ApiErrorResponse> infrastructure(Exception ex, HttpServletRequest request) {
        return resolverConhecidaOuInterna(ex, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> runtime(RuntimeException ex, HttpServletRequest request) {
        return resolverConhecidaOuInterna(ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> generic(Exception ex, HttpServletRequest request) {
        log.error("Erro nao tratado em {}", request.getRequestURI(), ex);
        return build(500, "INTERNAL_ERROR", mensagemInterna(ex), HINT_PADRAO, request);
    }

    private ResponseEntity<ApiErrorResponse> resolverConhecidaOuInterna(Throwable ex, HttpServletRequest request) {
        return ExceptionCauseResolver.findCause(ex, ResourceNotFoundException.class)
                .map(cause -> build(404, "NOT_FOUND", cause.getMessage(), null, request))
                .orElseGet(() -> ExceptionCauseResolver.findCause(ex, UnauthorizedException.class)
                        .map(cause -> build(401, "UNAUTHORIZED", cause.getMessage(), null, request))
                        .orElseGet(() -> ExceptionCauseResolver.findCause(ex, ConflictException.class)
                                .map(cause -> build(409, "CONFLICT", cause.getMessage(), null, request))
                                .orElseGet(() -> ExceptionCauseResolver.findCause(ex, BusinessException.class)
                                        .map(cause -> build(422, "BUSINESS_ERROR", cause.getMessage(), null, request))
                                        .orElseGet(() -> {
                                            log.error("Erro interno em {}", request.getRequestURI(), ex);
                                            return build(500, "INTERNAL_ERROR", mensagemInterna(ex), HINT_PADRAO, request);
                                        }))));
    }

    private String mensagemInterna(Throwable ex) {
        if (exposeErrorDetails) {
            Throwable root = ExceptionCauseResolver.rootCause(ex);
            if (root != null && root.getMessage() != null && !root.getMessage().isBlank()) {
                return root.getMessage();
            }
        }
        return "Ocorreu um erro inesperado ao processar a solicitacao.";
    }

    private String mensagemIntegridade(DataIntegrityViolationException ex) {
        if (exposeErrorDetails) {
            Throwable root = ExceptionCauseResolver.rootCause(ex);
            if (root != null && root.getMessage() != null && !root.getMessage().isBlank()) {
                return root.getMessage();
            }
        }
        return "Nao foi possivel salvar os dados. Verifique os campos obrigatorios e tente novamente.";
    }

    private ResponseEntity<ApiErrorResponse> build(
            int status,
            String error,
            String message,
            String hint,
            HttpServletRequest request) {
        return ResponseEntity.status(status).body(ApiErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .hint(hint)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
