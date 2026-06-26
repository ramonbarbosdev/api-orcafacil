package com.api_orcafacil.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.api_orcafacil.dto.OrganizacaoLoginDTO;

@Component
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class CentralAuthDirectory implements AuthDirectory {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CentralAuthDirectory(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<AuthDirectoryUser> buscarUsuarioAtivoPorCpf(String nuCpf) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select id_usuario, nu_cpf, nm_usuario, nm_email, ds_senha, tp_global, fl_ativo
                    from usuario_global
                    where nu_cpf = :nuCpf and fl_ativo = true
                    """,
                    Map.of("nuCpf", nuCpf),
                    (rs, rowNum) -> new AuthDirectoryUser(
                            rs.getLong("id_usuario"),
                            rs.getString("nu_cpf"),
                            rs.getString("nm_usuario"),
                            rs.getString("nm_email"),
                            rs.getString("ds_senha"),
                            rs.getString("tp_global"),
                            rs.getBoolean("fl_ativo"))));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrganizacaoLoginDTO> listarOrganizacoesAtivas(Long idUsuario) {
        return jdbcTemplate.query("""
                select o.id_organizacao, o.nm_organizacao, uo.ds_role
                from usuario_organizacao uo
                join organizacao o on o.id_organizacao = uo.id_organizacao
                where uo.id_usuario = :idUsuario
                  and uo.fl_ativo = true
                  and o.fl_ativo = true
                  and o.status = 'ATIVA'
                order by o.nm_organizacao
                """,
                Map.of("idUsuario", idUsuario),
                (rs, rowNum) -> new OrganizacaoLoginDTO(
                        rs.getLong("id_organizacao"),
                        rs.getString("nm_organizacao"),
                        rs.getString("ds_role")));
    }

    @Override
    public Optional<AuthOrganizationMembership> buscarVinculoAtivo(Long idUsuario, Long idOrganizacao) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                    select uo.id_usuario_organizacao, uo.id_usuario, o.id_organizacao, o.nm_organizacao, uo.ds_role
                    from usuario_organizacao uo
                    join organizacao o on o.id_organizacao = uo.id_organizacao
                    where uo.id_usuario = :idUsuario
                      and uo.id_organizacao = :idOrganizacao
                      and uo.fl_ativo = true
                      and o.fl_ativo = true
                      and o.status = 'ATIVA'
                    """,
                    Map.of("idUsuario", idUsuario, "idOrganizacao", idOrganizacao),
                    (rs, rowNum) -> new AuthOrganizationMembership(
                            rs.getLong("id_usuario_organizacao"),
                            rs.getLong("id_usuario"),
                            rs.getLong("id_organizacao"),
                            rs.getString("nm_organizacao"),
                            rs.getString("ds_role"))));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> listarPermissoes(Long idUsuario, Long idOrganizacao, String role) {
        LinkedHashSet<String> tetoPlano = new LinkedHashSet<>(permissoesPorPlano(idOrganizacao));
        LinkedHashSet<String> permissoes = new LinkedHashSet<>();

        for (String chave : permissoesPorPapelPadrao(role)) {
            if (tetoPlano.contains(chave)) {
                permissoes.add(chave);
            }
        }
        for (String chave : permissoesPorUsuario(idUsuario, idOrganizacao)) {
            if (tetoPlano.contains(chave)) {
                permissoes.add(chave);
            }
        }
        return List.copyOf(permissoes);
    }

    private List<String> permissoesPorPapelPadrao(String role) {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return jdbcTemplate.queryForList("""
                select distinct pg.nm_chave
                from papel_permissao_padrao ppp
                join papel p on p.id_papel = ppp.id_papel
                join permissao_global pg on pg.id_permissao = ppp.id_permissao
                where p.nm_papel = :role
                  and p.fl_ativo = true
                  and pg.fl_ativo = true
                order by pg.nm_chave
                """,
                Map.of("role", role),
                String.class);
    }

    private List<String> permissoesPorPlano(Long idOrganizacao) {
        try {
            Long idPlano = jdbcTemplate.queryForObject("""
                    select id_planoassinatura from organizacao where id_organizacao = :idOrganizacao
                    """,
                    Map.of("idOrganizacao", idOrganizacao),
                    Long.class);

            if (idPlano == null) {
                return listarTodasPermissoesAtivas();
            }

            List<String> chaves = jdbcTemplate.queryForList("""
                    select distinct pg.nm_chave
                    from plano_permissao pp
                    join permissao_global pg on pg.id_permissao = pp.id_permissao
                    where pp.id_planoassinatura = :idPlano
                      and pg.fl_ativo = true
                    order by pg.nm_chave
                    """,
                    Map.of("idPlano", idPlano),
                    String.class);

            return chaves.isEmpty() ? listarTodasPermissoesAtivas() : chaves;
        } catch (EmptyResultDataAccessException ex) {
            return listarTodasPermissoesAtivas();
        }
    }

    private List<String> listarTodasPermissoesAtivas() {
        return jdbcTemplate.queryForList("""
                select nm_chave from permissao_global where fl_ativo = true order by nm_chave
                """,
                Map.of(),
                String.class);
    }

    private List<String> permissoesPorUsuario(Long idUsuario, Long idOrganizacao) {
        return jdbcTemplate.queryForList("""
                select distinct pg.nm_chave
                from usuario_permissao up
                join permissao_global pg on pg.id_permissao = up.id_permissao
                where up.id_usuario = :idUsuario
                  and up.id_organizacao = :idOrganizacao
                  and pg.fl_ativo = true
                order by pg.nm_chave
                """,
                Map.of("idUsuario", idUsuario, "idOrganizacao", idOrganizacao),
                String.class);
    }
}
