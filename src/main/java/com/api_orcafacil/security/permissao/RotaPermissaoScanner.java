package com.api_orcafacil.security.permissao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.api_orcafacil.dto.permissao.RecursoDescobertoDTO;

@Component
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class RotaPermissaoScanner {

    private final RequestMappingHandlerMapping handlerMapping;

    public RotaPermissaoScanner(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public List<RecursoDescobertoDTO> descobrir() {
        Set<String> vistos = new LinkedHashSet<>();
        List<RecursoDescobertoDTO> resultado = new ArrayList<>();

        for (RequestMappingInfo info : handlerMapping.getHandlerMethods().keySet()) {
            HandlerMethod handler = handlerMapping.getHandlerMethods().get(info);
            if (handler == null) {
                continue;
            }
            String pacote = handler.getBeanType().getPackageName();
            if (pacote.contains(".controller.admin.")) {
                continue;
            }
            for (String pattern : info.getPatternValues()) {
                String modulo = moduloDaRota(pattern);
                if (modulo == null || PermissaoCatalogoCurado.isReservado(modulo) || !vistos.add(modulo)) {
                    continue;
                }
                resultado.add(new RecursoDescobertoDTO(modulo, rotaBase(pattern), "DESCOBERTO"));
            }
        }

        resultado.sort(Comparator.comparing(RecursoDescobertoDTO::modulo));
        return resultado;
    }

    private String moduloDaRota(String pattern) {
        if (pattern == null || pattern.isBlank() || "/".equals(pattern)) {
            return null;
        }
        String normalizado = pattern.startsWith("/") ? pattern.substring(1) : pattern;
        int slash = normalizado.indexOf('/');
        String modulo = slash < 0 ? normalizado : normalizado.substring(0, slash);
        if (modulo.contains("{")) {
            return null;
        }
        return modulo;
    }

    private String rotaBase(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return "/";
        }
        String normalizado = pattern.startsWith("/") ? pattern : "/" + pattern;
        int slash = normalizado.indexOf('/', 1);
        return slash < 0 ? normalizado : normalizado.substring(0, slash);
    }
}
