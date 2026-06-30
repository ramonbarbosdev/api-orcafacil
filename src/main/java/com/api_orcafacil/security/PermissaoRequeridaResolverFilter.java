package com.api_orcafacil.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.api_orcafacil.security.permissao.PermissaoNormalizador;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PermissaoRequeridaResolverFilter extends OncePerRequestFilter {

    private final RequestMappingHandlerMapping handlerMapping;

    public PermissaoRequeridaResolverFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        resolverAnotacao(request);
        filterChain.doFilter(request, response);
    }

    private void resolverAnotacao(HttpServletRequest request) {
        try {
            HandlerExecutionChain chain = handlerMapping.getHandler(request);
            if (chain == null || !(chain.getHandler() instanceof HandlerMethod handlerMethod)) {
                return;
            }

            RequerPermissao anotacao = handlerMethod.getMethodAnnotation(RequerPermissao.class);
            if (anotacao == null) {
                anotacao = handlerMethod.getBeanType().getAnnotation(RequerPermissao.class);
            }
            if (anotacao == null) {
                return;
            }

            String modulo = PermissaoNormalizador.normalizarModulo(anotacao.modulo());
            String acao = PermissaoNormalizador.normalizarAcao(anotacao.acao());
            request.setAttribute(
                    PermissaoRequeridaContext.REQUEST_ATTR,
                    new PermissaoRequeridaContext(modulo, acao));
        } catch (Exception ignored) {
            // Sem handler mapeado: o filtro dinâmico segue com URL + método HTTP.
        }
    }
}
