package com.api_orcafacil.security;

import java.io.IOException;
import java.time.LocalDateTime;

import com.api_orcafacil.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class SecurityErrorResponses {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private SecurityErrorResponses() {
    }

    public static void escrever(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String hint) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .hint(hint)
                .path(request != null ? request.getRequestURI() : null)
                .timestamp(LocalDateTime.now())
                .build();
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }

    public static void escreverAcessoNegado(
            HttpServletRequest request,
            HttpServletResponse response,
            String modulo,
            String acao,
            String permissaoEsperada) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpServletResponse.SC_FORBIDDEN)
                .error("ACCESS_DENIED")
                .message(PermissaoMensagemUtil.mensagemAcessoNegado(permissaoEsperada))
                .hint("Solicite ao administrador da sua organização a liberação deste acesso.")
                .path(request != null ? request.getRequestURI() : null)
                .timestamp(LocalDateTime.now())
                .modulo(modulo)
                .acao(acao)
                .permissaoEsperada(permissaoEsperada)
                .build();
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }
}
