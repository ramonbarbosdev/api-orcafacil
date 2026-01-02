package com.api_orcafacil.domain.sistema.model;


import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp
) {}