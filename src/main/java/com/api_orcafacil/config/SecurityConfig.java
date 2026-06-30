package com.api_orcafacil.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.api_orcafacil.security.DynamicRoutePermissionFilter;
import com.api_orcafacil.security.JwtAuthenticationFilter;
import com.api_orcafacil.security.PermissaoRequeridaResolverFilter;
import com.api_orcafacil.security.SecurityErrorResponses;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            PermissaoRequeridaResolverFilter permissaoRequeridaResolverFilter,
            DynamicRoutePermissionFilter dynamicRoutePermissionFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orcamentos/visualizacao/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orcamentos/relatorio/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(adminPathMatcher())
                                .hasAuthority("GLOBAL_SUPER_ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            SecurityErrorResponses.escrever(
                                    request,
                                    response,
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    "UNAUTHORIZED",
                                    "Autenticação necessária.",
                                    "Faça login para continuar utilizando o sistema.");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            boolean admin = isAdminPath(request);
                            if (admin) {
                                SecurityErrorResponses.escrever(
                                        request,
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "PLATFORM_ADMIN_REQUIRED",
                                        "Esta área é exclusiva para administradores da plataforma.",
                                        "Se você precisa de acesso administrativo, entre em contato com o suporte.");
                            } else {
                                SecurityErrorResponses.escrever(
                                        request,
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "ACCESS_DENIED",
                                        "Você não tem permissão para realizar esta ação.",
                                        "Solicite ao administrador da sua organização a liberação deste acesso.");
                            }
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(permissaoRequeridaResolverFilter, JwtAuthenticationFilter.class)
                .addFilterAfter(dynamicRoutePermissionFilter, PermissaoRequeridaResolverFilter.class)
                .build();
    }

    private static boolean isAdminPath(jakarta.servlet.http.HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (servletPath != null && servletPath.startsWith("/admin")) {
            return true;
        }
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && uri.startsWith(context)) {
            return uri.substring(context.length()).startsWith("/admin");
        }
        return uri.startsWith("/admin");
    }

    private static RequestMatcher adminPathMatcher() {
        return request -> {
            String servletPath = request.getServletPath();
            if (servletPath != null && servletPath.startsWith("/admin")) {
                return true;
            }
            String uri = request.getRequestURI();
            String context = request.getContextPath();
            if (context != null && !context.isEmpty() && uri.startsWith(context)) {
                return uri.substring(context.length()).startsWith("/admin");
            }
            return uri.startsWith("/admin");
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "https://orcafacil.ramoncode.com.br",
                "https://api-orcafacil.ramoncode.com.br"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    FilterRegistrationBean<DynamicRoutePermissionFilter> dynamicFilterRegistration(
            DynamicRoutePermissionFilter filter) {
        FilterRegistrationBean<DynamicRoutePermissionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    FilterRegistrationBean<PermissaoRequeridaResolverFilter> permissaoResolverFilterRegistration(
            PermissaoRequeridaResolverFilter filter) {
        FilterRegistrationBean<PermissaoRequeridaResolverFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
