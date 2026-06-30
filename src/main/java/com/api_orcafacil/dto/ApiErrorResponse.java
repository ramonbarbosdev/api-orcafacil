package com.api_orcafacil.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String hint;
    private final String path;
    private final LocalDateTime timestamp;
    private final String modulo;
    private final String acao;
    private final String permissaoEsperada;
}
