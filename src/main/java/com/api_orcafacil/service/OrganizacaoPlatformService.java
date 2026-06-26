package com.api_orcafacil.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.api_orcafacil.common.SlugUtil;
import com.api_orcafacil.dto.OrganizacaoRequestDTO;
import com.api_orcafacil.dto.OrganizacaoResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioRequestDTO;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.provisioning.TenantProvisioningPlan;
import com.api_orcafacil.provisioning.TenantProvisioningService;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class OrganizacaoPlatformService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TenantProvisioningService tenantProvisioningService;
    private final boolean autoProvisioningEnabled;

    public OrganizacaoPlatformService(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            ObjectProvider<TenantProvisioningService> tenantProvisioningService,
            @org.springframework.beans.factory.annotation.Value("${app.saas.provisioning.auto-enabled:true}") boolean autoProvisioningEnabled) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.tenantProvisioningService = tenantProvisioningService.getIfAvailable();
        this.autoProvisioningEnabled = autoProvisioningEnabled;
    }

    public List<OrganizacaoResponseDTO> listar() {
        return jdbcTemplate.query("""
                select id_organizacao, nm_organizacao, slug, database_name, status, fl_ativo, dt_criacao
                from organizacao order by nm_organizacao
                """,
                Map.of(),
                (rs, rowNum) -> mapOrganizacao(rs));
    }

    public OrganizacaoResponseDTO criar(OrganizacaoRequestDTO request) {
        String slug = SlugUtil.fromNome(request.nmOrganizacao());
        String databaseName = SlugUtil.databaseName(slug);

        if (existeOrganizacao(slug, databaseName)) {
            throw new ConflictException("Ja existe organizacao com este nome");
        }

        Long id = jdbcTemplate.queryForObject("select nextval('seq_central_organizacao')", Map.of(), Long.class);

        OrganizacaoResponseDTO response = jdbcTemplate.queryForObject("""
                insert into organizacao (
                    id_organizacao, slug, nm_organizacao, ds_documento, status,
                    database_name, storage_mode, fl_ativo, dt_criacao, dt_atualizacao
                )
                values (
                    :id, :slug, :nome, :documento, 'EM_PROVISIONAMENTO',
                    :databaseName, 'DATABASE_PER_ORG', true, now(), now()
                )
                returning id_organizacao, nm_organizacao, slug, database_name, status, fl_ativo, dt_criacao
                """,
                Map.of(
                        "id", id,
                        "slug", slug,
                        "nome", request.nmOrganizacao(),
                        "documento", request.dsDocumento(),
                        "databaseName", databaseName),
                (rs, rowNum) -> mapOrganizacao(rs));

        agendarProvisionamento(id, slug, databaseName);
        return response;
    }

    public void vincularUsuario(Long idOrganizacao, VinculoUsuarioRequestDTO request) {
        buscarOrganizacao(idOrganizacao);

        List<Long> existentes = jdbcTemplate.query("""
                select id_usuario from usuario_global where nu_cpf = :cpf
                """,
                Map.of("cpf", request.nuCpf()),
                (rs, rowNum) -> rs.getLong("id_usuario"));

        Long idUsuario = existentes.isEmpty()
                ? jdbcTemplate.queryForObject("""
                        insert into usuario_global (nu_cpf, nm_usuario, ds_senha, tp_global, fl_ativo)
                        values (:cpf, :nome, :senha, 'DEFAULT', true)
                        returning id_usuario
                        """,
                        Map.of(
                                "cpf", request.nuCpf(),
                                "nome", request.nmUsuario(),
                                "senha", passwordEncoder.encode(request.dsSenha())),
                        Long.class)
                : existentes.get(0);

        jdbcTemplate.update("""
                insert into usuario_organizacao (id_usuario, id_organizacao, ds_role, fl_ativo)
                values (:idUsuario, :idOrganizacao, :role, true)
                on conflict (id_usuario, id_organizacao)
                do update set ds_role = excluded.ds_role, fl_ativo = true
                """,
                Map.of(
                        "idUsuario", idUsuario,
                        "idOrganizacao", idOrganizacao,
                        "role", request.dsRole()));
    }

    private void buscarOrganizacao(Long idOrganizacao) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from organizacao where id_organizacao = :id",
                Map.of("id", idOrganizacao),
                Integer.class);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Organizacao nao encontrada");
        }
    }

    private boolean existeOrganizacao(String slug, String databaseName) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from organizacao where slug = :slug or database_name = :databaseName
                """,
                Map.of("slug", slug, "databaseName", databaseName),
                Integer.class);
        return count != null && count > 0;
    }

    private void agendarProvisionamento(Long idOrganizacao, String slug, String databaseName) {
        if (!autoProvisioningEnabled || tenantProvisioningService == null) {
            return;
        }
        TenantProvisioningPlan plan = new TenantProvisioningPlan(idOrganizacao, slug, databaseName);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                tenantProvisioningService.provisionar(plan);
            }
        });
    }

    private OrganizacaoResponseDTO mapOrganizacao(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new OrganizacaoResponseDTO(
                rs.getLong("id_organizacao"),
                rs.getString("nm_organizacao"),
                rs.getString("slug"),
                rs.getString("database_name"),
                rs.getString("status"),
                rs.getBoolean("fl_ativo"),
                rs.getObject("dt_criacao", java.time.LocalDateTime.class));
    }
}
