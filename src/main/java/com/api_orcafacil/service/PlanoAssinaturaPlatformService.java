package com.api_orcafacil.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.PlanoAssinaturaRequest;
import com.api_orcafacil.dto.precificacao.PlanoAssinaturaResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class PlanoAssinaturaPlatformService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlanoAssinaturaPlatformService(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PlanoAssinaturaResponse> listar() {
        return jdbcTemplate.query("""
                select id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                       nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                from plano_assinatura order by nm_planoassinatura
                """, Map.of(), this::map);
    }

    public PlanoAssinaturaResponse buscar(Long id) {
        List<PlanoAssinaturaResponse> lista = jdbcTemplate.query("""
                select id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                       nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                from plano_assinatura where id_planoassinatura = :id
                """, Map.of("id", id), this::map);
        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("Plano nao encontrado");
        }
        return lista.get(0);
    }

    public PlanoAssinaturaResponse criar(PlanoAssinaturaRequest request) {
        return jdbcTemplate.queryForObject("""
                insert into plano_assinatura (nm_planoassinatura, vl_mensal, nu_limitemensagens, nu_limiteatendentes, fl_ativo)
                values (:nome, :valor, :limiteMsg, :limiteAtend, coalesce(:ativo, true))
                returning id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                          nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                """,
                Map.of(
                        "nome", request.getNmPlanoAssinatura(),
                        "valor", request.getVlMensal(),
                        "limiteMsg", request.getNuLimiteMensagens() != null ? request.getNuLimiteMensagens() : 0,
                        "limiteAtend", request.getNuLimiteAtendentes() != null ? request.getNuLimiteAtendentes() : 0,
                        "ativo", request.getFlAtivo()),
                this::map);
    }

    public PlanoAssinaturaResponse atualizar(Long id, PlanoAssinaturaRequest request) {
        buscar(id);
        return jdbcTemplate.queryForObject("""
                update plano_assinatura set
                    nm_planoassinatura = :nome,
                    vl_mensal = :valor,
                    nu_limitemensagens = :limiteMsg,
                    nu_limiteatendentes = :limiteAtend,
                    fl_ativo = coalesce(:ativo, fl_ativo),
                    dt_atualizacao = now()
                where id_planoassinatura = :id
                returning id_planoassinatura, nm_planoassinatura, vl_mensal, nu_limitemensagens,
                          nu_limiteatendentes, fl_ativo, dt_criacao, dt_atualizacao
                """,
                Map.of(
                        "id", id,
                        "nome", request.getNmPlanoAssinatura(),
                        "valor", request.getVlMensal(),
                        "limiteMsg", request.getNuLimiteMensagens(),
                        "limiteAtend", request.getNuLimiteAtendentes(),
                        "ativo", request.getFlAtivo()),
                this::map);
    }

    public void excluir(Long id) {
        buscar(id);
        jdbcTemplate.update("delete from plano_assinatura where id_planoassinatura = :id", Map.of("id", id));
    }

    private PlanoAssinaturaResponse map(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        PlanoAssinaturaResponse r = new PlanoAssinaturaResponse();
        r.setIdPlanoAssinatura(rs.getLong("id_planoassinatura"));
        r.setNmPlanoAssinatura(rs.getString("nm_planoassinatura"));
        r.setVlMensal(rs.getObject("vl_mensal") != null ? rs.getDouble("vl_mensal") : null);
        r.setNuLimiteMensagens(rs.getInt("nu_limitemensagens"));
        r.setNuLimiteAtendentes(rs.getInt("nu_limiteatendentes"));
        r.setFlAtivo(rs.getBoolean("fl_ativo"));
        r.setDtCriacao(rs.getObject("dt_criacao", java.time.LocalDateTime.class));
        r.setDtAtualizacao(rs.getObject("dt_atualizacao", java.time.LocalDateTime.class));
        return r;
    }
}
