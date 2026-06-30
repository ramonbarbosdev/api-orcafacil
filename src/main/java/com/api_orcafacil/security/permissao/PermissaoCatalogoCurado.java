package com.api_orcafacil.security.permissao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.api_orcafacil.dto.permissao.CatalogoRecursoCuradoDTO;

public final class PermissaoCatalogoCurado {

    private static final Set<String> MODULOS_RESERVADOS = Set.of("auth", "admin", "error", "swagger-ui", "v3");

    private static final List<CatalogoRecursoCuradoDTO> ITENS = List.of(
            item("clientes", "Clientes", "/clientes", "Operacional", crudCompleto()),
            item("catalogos", "Catálogos", "/catalogos", "Operacional", crudCompleto()),
            item("servicos", "Serviços", "/servicos", "Operacional", crudCompleto()),
            item("categorias-servico", "Categorias de serviço", "/categorias-servico", "Operacional", crudCompleto()),
            item("orcamentos", "Orçamentos", "/orcamentos", "Operacional", crudCompleto()),
            item("condicoes-pagamento", "Condições de pagamento", "/condicoes-pagamento", "Operacional", crudCompleto()),
            item("configuracao-orcamento", "Configuração de orçamento", "/configuracao-orcamento", "Configurações", List.of("exibir", "ler", "editar")),
            item("metodos-precificacao", "Métodos de precificação", "/metodos-precificacao", "Configurações", crudCompleto()),
            item("campos-personalizados", "Campos personalizados", "/campos-personalizados", "Configurações", crudCompleto()),
            item("metodos-ajuste", "Métodos de ajuste", "/metodos-ajuste", "Configurações", crudCompleto()),
            item("empresa-metodos-precificacao", "Métodos da empresa", "/empresa-metodos-precificacao", "Configurações", crudCompleto()),
            item("organizacao", "Logo da organização", "/organizacao", "Configurações", crudCompleto()),
            item("perfil", "Perfil do usuário", "/perfil", "Conta", crudCompleto()),
            item("politica-plano", "Política do plano", "/politica-plano", "Conta", List.of("ler")),
            item("dashboard", "Dashboard", "/dashboard", "Dashboards", List.of("exibir", "ler")),
            item("relatorios", "Relatórios", "/relatorios", "Dashboards", List.of("exibir", "ler")));

    private PermissaoCatalogoCurado() {
    }

    public static List<CatalogoRecursoCuradoDTO> listar() {
        return ITENS;
    }

    public static Optional<CatalogoRecursoCuradoDTO> buscar(String modulo) {
        return ITENS.stream()
                .filter(i -> i.modulo().equals(modulo))
                .findFirst();
    }

    public static boolean isReservado(String modulo) {
        return MODULOS_RESERVADOS.contains(modulo);
    }

    public static Map<String, String> rotulosPorModulo() {
        return ITENS.stream()
                .collect(java.util.stream.Collectors.toMap(
                        CatalogoRecursoCuradoDTO::modulo,
                        CatalogoRecursoCuradoDTO::label,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new));
    }

    private static List<String> crudCompleto() {
        return List.of("exibir", "ler", "criar", "editar", "deletar");
    }

    private static CatalogoRecursoCuradoDTO item(
            String modulo,
            String label,
            String rota,
            String grupo,
            List<String> acoes) {
        return new CatalogoRecursoCuradoDTO(modulo, label, rota, grupo, acoes);
    }
}
