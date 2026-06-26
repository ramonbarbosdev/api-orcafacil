package com.api_orcafacil.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.PermissaoItemDTO;
import com.api_orcafacil.dto.PermissaoModuloDTO;
import com.api_orcafacil.exception.BusinessException;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class PermissaoPlatformService {

    private static final Map<String, String> ROTULOS_MODULO = Map.ofEntries(
            Map.entry("clientes", "Clientes"),
            Map.entry("catalogos", "Catálogos"),
            Map.entry("servicos", "Serviços"),
            Map.entry("categorias-servico", "Categorias de serviço"),
            Map.entry("orcamentos", "Orçamentos"),
            Map.entry("condicoes-pagamento", "Condições de pagamento"),
            Map.entry("configuracao-orcamento", "Configuração de orçamento"),
            Map.entry("metodos-precificacao", "Métodos de precificação"),
            Map.entry("campos-personalizados", "Campos personalizados"),
            Map.entry("metodos-ajuste", "Métodos de ajuste"),
            Map.entry("empresa-metodos-precificacao", "Métodos da empresa"),
            Map.entry("perfil", "Perfil"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PermissaoPlatformService(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<PermissaoModuloDTO> listarCatalogo() {
        List<PermissaoItemDTO> itens = jdbcTemplate.query("""
                select id_permissao, nm_permissao, nm_chave
                from permissao_global
                where fl_ativo = true
                order by nm_chave
                """,
                Map.of(),
                (rs, rowNum) -> {
                    String chave = rs.getString("nm_chave");
                    int idx = chave.lastIndexOf('.');
                    String acao = idx >= 0 ? chave.substring(idx + 1) : chave;
                    return new PermissaoItemDTO(
                            rs.getLong("id_permissao"),
                            rs.getString("nm_permissao"),
                            chave,
                            acao);
                });

        LinkedHashMap<String, List<PermissaoItemDTO>> porModulo = new LinkedHashMap<>();
        for (PermissaoItemDTO item : itens) {
            String modulo = moduloDaChave(item.nmChave());
            porModulo.computeIfAbsent(modulo, k -> new ArrayList<>()).add(item);
        }

        return porModulo.entrySet().stream()
                .map(e -> new PermissaoModuloDTO(
                        e.getKey(),
                        rotuloModulo(e.getKey()),
                        List.copyOf(e.getValue())))
                .toList();
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<String> listarChavesAtivas() {
        return jdbcTemplate.queryForList("""
                select nm_chave from permissao_global where fl_ativo = true order by nm_chave
                """,
                Map.of(),
                String.class);
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<Long> resolverIdsPorChaves(List<String> chaves) {
        if (chaves == null || chaves.isEmpty()) {
            return List.of();
        }
        Set<String> unicas = chaves.stream()
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (unicas.isEmpty()) {
            return List.of();
        }

        List<Long> ids = jdbcTemplate.queryForList("""
                select id_permissao
                from permissao_global
                where fl_ativo = true and nm_chave in (:chaves)
                order by id_permissao
                """,
                Map.of("chaves", unicas),
                Long.class);

        if (ids.size() != unicas.size()) {
            throw new BusinessException("Uma ou mais permissoes informadas sao invalidas");
        }
        return ids;
    }

    void substituirPermissoesPapel(Long idPapel, List<String> chaves) {
        validarPapel(idPapel);
        List<Long> ids = resolverIdsPorChaves(chaves);

        jdbcTemplate.update("delete from papel_permissao_padrao where id_papel = :idPapel",
                Map.of("idPapel", idPapel));

        for (Long idPermissao : ids) {
            jdbcTemplate.update("""
                    insert into papel_permissao_padrao (id_papel, id_permissao)
                    values (:idPapel, :idPermissao)
                    on conflict do nothing
                    """,
                    Map.of("idPapel", idPapel, "idPermissao", idPermissao));
        }
    }

    void substituirPermissoesPlano(Long idPlano, List<String> chaves) {
        List<Long> ids = resolverIdsPorChaves(chaves);

        jdbcTemplate.update("delete from plano_permissao where id_planoassinatura = :idPlano",
                Map.of("idPlano", idPlano));

        for (Long idPermissao : ids) {
            jdbcTemplate.update("""
                    insert into plano_permissao (id_planoassinatura, id_permissao)
                    values (:idPlano, :idPermissao)
                    on conflict do nothing
                    """,
                    Map.of("idPlano", idPlano, "idPermissao", idPermissao));
        }
    }

    void concederTodasPermissoesPlano(Long idPlano) {
        jdbcTemplate.update("""
                insert into plano_permissao (id_planoassinatura, id_permissao)
                select :idPlano, id_permissao
                from permissao_global
                where fl_ativo = true
                on conflict do nothing
                """,
                Map.of("idPlano", idPlano));
    }

    private void validarPapel(Long idPapel) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from papel where id_papel = :id and fl_ativo = true",
                Map.of("id", idPapel),
                Integer.class);
        if (count == null || count == 0) {
            throw new BusinessException("Papel nao encontrado");
        }
    }

    static String moduloDaChave(String chave) {
        int idx = chave.lastIndexOf('.');
        return idx >= 0 ? chave.substring(0, idx) : chave;
    }

    static String rotuloModulo(String modulo) {
        if (ROTULOS_MODULO.containsKey(modulo)) {
            return ROTULOS_MODULO.get(modulo);
        }
        String legivel = modulo.replace('-', ' ');
        if (legivel.isEmpty()) {
            return modulo;
        }
        return Character.toUpperCase(legivel.charAt(0)) + legivel.substring(1);
    }
}
