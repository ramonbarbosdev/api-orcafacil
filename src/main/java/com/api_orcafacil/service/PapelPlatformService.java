package com.api_orcafacil.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.PapelDetalheDTO;
import com.api_orcafacil.dto.PapelResponseDTO;
import com.api_orcafacil.exception.ResourceNotFoundException;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class PapelPlatformService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PermissaoPlatformService permissaoPlatformService;

    public PapelPlatformService(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            PermissaoPlatformService permissaoPlatformService) {
        this.jdbcTemplate = jdbcTemplate;
        this.permissaoPlatformService = permissaoPlatformService;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public List<PapelResponseDTO> listar() {
        return jdbcTemplate.query("""
                select p.id_papel, p.nm_papel, p.fl_ativo,
                       coalesce(count(ppp.id_permissao), 0) as total_permissoes
                from papel p
                left join papel_permissao_padrao ppp on ppp.id_papel = p.id_papel
                where p.fl_ativo = true
                group by p.id_papel, p.nm_papel, p.fl_ativo
                order by p.nm_papel
                """,
                Map.of(),
                (rs, rowNum) -> new PapelResponseDTO(
                        rs.getLong("id_papel"),
                        rs.getString("nm_papel"),
                        rs.getBoolean("fl_ativo"),
                        rs.getInt("total_permissoes")));
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public PapelDetalheDTO buscar(Long idPapel) {
        List<PapelDetalheDTO> lista = jdbcTemplate.query("""
                select p.id_papel, p.nm_papel
                from papel p
                where p.id_papel = :id and p.fl_ativo = true
                """,
                Map.of("id", idPapel),
                (rs, rowNum) -> new PapelDetalheDTO(
                        rs.getLong("id_papel"),
                        rs.getString("nm_papel"),
                        listarChaves(idPapel)));
        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("Papel nao encontrado");
        }
        return lista.get(0);
    }

    public PapelDetalheDTO atualizarPermissoes(Long idPapel, List<String> chaves) {
        buscar(idPapel);
        permissaoPlatformService.substituirPermissoesPapel(idPapel, chaves);
        return buscar(idPapel);
    }

    private List<String> listarChaves(Long idPapel) {
        return jdbcTemplate.queryForList("""
                select pg.nm_chave
                from papel_permissao_padrao ppp
                join permissao_global pg on pg.id_permissao = ppp.id_permissao
                where ppp.id_papel = :idPapel and pg.fl_ativo = true
                order by pg.nm_chave
                """,
                Map.of("idPapel", idPapel),
                String.class);
    }
}
