package com.api_orcafacil.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DynamicRoutePermissionFilter extends OncePerRequestFilter {

    private static final Set<String> ROTAS_IGNORADAS = Set.of("auth", "admin", "error");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String acao = acaoPorMetodo(request.getMethod());
        String modulo = moduloDaRota(request.getServletPath());

        if (acao == null || modulo == null || ROTAS_IGNORADAS.contains(modulo)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (temAuthority(authentication, "GLOBAL_SUPER_ADMIN")) {
            filterChain.doFilter(request, response);
            return;
        }

        String permissaoNecessaria = modulo + "." + acao;
        if (!temAuthority(authentication, "TENANT_ACCESS") || !temAuthority(authentication, permissaoNecessaria)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {"status":403,"error":"ACCESS_DENIED","message":"Permissao necessaria: %s"}
                    """.formatted(permissaoNecessaria));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String acaoPorMetodo(String method) {
        return switch (method) {
            case "GET", "HEAD" -> "ler";
            case "POST" -> "criar";
            case "PUT", "PATCH" -> "editar";
            case "DELETE" -> "deletar";
            default -> null;
        };
    }

    private String moduloDaRota(String servletPath) {
        if (servletPath == null || servletPath.isBlank() || "/".equals(servletPath)) {
            return null;
        }
        String path = servletPath.startsWith("/") ? servletPath.substring(1) : servletPath;
        int slash = path.indexOf('/');
        return slash < 0 ? path : path.substring(0, slash);
    }

    private boolean temAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }
}
