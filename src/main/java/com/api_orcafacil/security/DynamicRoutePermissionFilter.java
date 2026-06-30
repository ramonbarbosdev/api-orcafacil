package com.api_orcafacil.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api_orcafacil.security.permissao.PermissaoAcaoHttp;
import com.api_orcafacil.security.permissao.PermissaoCatalogoCurado;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DynamicRoutePermissionFilter extends OncePerRequestFilter {

    private static final Set<String> MODULOS_PLATAFORMA = Set.of("auth", "admin", "error");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = pathSemContexto(request);
        if (isRotaPlataforma(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String modulo;
        String acao;

        Object override = request.getAttribute(PermissaoRequeridaContext.REQUEST_ATTR);
        if (override instanceof PermissaoRequeridaContext ctx) {
            modulo = ctx.getModulo();
            acao = ctx.getAcao();
        } else {
            acao = PermissaoAcaoHttp.acaoPorMetodoHttp(request.getMethod()).orElse(null);
            modulo = moduloDaRota(path);
        }

        if (acao == null || modulo == null || MODULOS_PLATAFORMA.contains(modulo)
                || PermissaoCatalogoCurado.isReservado(modulo)) {
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

        if (!temAuthority(authentication, "TENANT_ACCESS")) {
            SecurityErrorResponses.escrever(
                    request,
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "ORGANIZATION_REQUIRED",
                    "Selecione uma organização para continuar.",
                    "Escolha a empresa na qual deseja trabalhar antes de acessar este recurso.");
            return;
        }

        if ("politica-plano".equals(modulo)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.endsWith("/sequencia") && "GET".equalsIgnoreCase(request.getMethod())) {
            String ler = modulo + ".ler";
            String criar = modulo + ".criar";
            if (!temAuthority(authentication, ler) && !temAuthority(authentication, criar)) {
                SecurityErrorResponses.escreverAcessoNegado(request, response, modulo, "ler", ler);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        String permissaoNecessaria = modulo + "." + acao;
        if (!temAuthority(authentication, permissaoNecessaria)) {
            SecurityErrorResponses.escreverAcessoNegado(
                    request, response, modulo, acao, permissaoNecessaria);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRotaPlataforma(String path) {
        return path.startsWith("/admin") || path.startsWith("/auth");
    }

    private String pathSemContexto(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private String moduloDaRota(String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return null;
        }
        String normalizado = path.startsWith("/") ? path.substring(1) : path;
        int slash = normalizado.indexOf('/');
        return slash < 0 ? normalizado : normalizado.substring(0, slash);
    }

    private boolean temAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }
}
