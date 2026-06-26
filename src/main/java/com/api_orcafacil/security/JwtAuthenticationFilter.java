package com.api_orcafacil.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api_orcafacil.tenant.OrganizationResolver;
import com.api_orcafacil.tenant.TenantDescriptor;
import com.api_orcafacil.tenant.TenantRuntimeContext;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthDirectory authDirectory;
    private final OrganizationResolver organizationResolver;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            AuthDirectory authDirectory,
            OrganizationResolver organizationResolver) {
        this.jwtService = jwtService;
        this.authDirectory = authDirectory;
        this.organizationResolver = organizationResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        TenantRuntimeContext.clear();

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parse(header.substring(7));
            Long idUsuario = Long.valueOf(claims.getSubject());
            String tipoGlobal = claims.get("tipoGlobal", String.class);
            Long idOrganizacao = extrairLong(claims, "idOrganizacao");
            String role = claims.get("role", String.class);
            List<String> permissoes = extrairPermissoes(claims);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            boolean superAdmin = "SUPER_ADMIN".equals(tipoGlobal);

            if (superAdmin) {
                idOrganizacao = null;
                role = null;
                permissoes = List.of();
                authorities.clear();
                authorities.add(new SimpleGrantedAuthority("GLOBAL_SUPER_ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("GLOBAL_" + tipoGlobal));
            }

            if (!superAdmin && idOrganizacao != null) {
                AuthOrganizationMembership vinculo = authDirectory
                        .buscarVinculoAtivo(idUsuario, idOrganizacao)
                        .orElse(null);
                if (vinculo == null) {
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }
                role = vinculo.dsRole();
                permissoes = authDirectory.listarPermissoes(idUsuario, idOrganizacao, role);
                authorities.add(new SimpleGrantedAuthority("TENANT_ACCESS"));
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }

            SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(
                    idUsuario, tipoGlobal, idOrganizacao, role, permissoes, authorities));

            if (idOrganizacao != null && !superAdmin) {
                TenantDescriptor descriptor = organizationResolver.resolver(idOrganizacao);
                TenantRuntimeContext.set(new TenantRuntimeContext.CurrentTenant(
                        idUsuario, idOrganizacao, role, permissoes, descriptor));
            }
        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
            TenantRuntimeContext.clear();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantRuntimeContext.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> extrairPermissoes(Claims claims) {
        Object permissoes = claims.get("permissoes");
        if (permissoes instanceof List<?> lista) {
            return lista.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private Long extrairLong(Claims claims, String chave) {
        Object valor = claims.get(chave);
        if (valor instanceof Number number) {
            return number.longValue();
        }
        if (valor instanceof String texto && !texto.isBlank()) {
            return Long.valueOf(texto);
        }
        return null;
    }
}
