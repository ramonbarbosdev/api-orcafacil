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
import org.springframework.util.StringUtils;

import com.api_orcafacil.common.SlugUtil;
import com.api_orcafacil.dto.OrganizacaoRequestDTO;
import com.api_orcafacil.dto.OrganizacaoResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioRequestDTO;
import com.api_orcafacil.dto.VinculoUsuarioResponseDTO;
import com.api_orcafacil.dto.VinculoUsuarioUpdateDTO;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.provisioning.TenantProvisioningPlan;
import com.api_orcafacil.provisioning.TenantProvisioningService;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
@Transactional(transactionManager = "centralTransactionManager")
public class OrganizacaoPlatformService {

    private static final String SELECT_ORGANIZACAO = """
            select id_organizacao, nm_organizacao, ds_documento, slug, database_name, status, fl_ativo, dt_criacao
            """;

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
        return jdbcTemplate.query(
                SELECT_ORGANIZACAO + " from organizacao order by nm_organizacao",
                Map.of(),
                (rs, rowNum) -> mapOrganizacao(rs));
    }

    public OrganizacaoResponseDTO buscar(Long idOrganizacao) {
        List<OrganizacaoResponseDTO> lista = jdbcTemplate.query(
                SELECT_ORGANIZACAO + " from organizacao where id_organizacao = :id",
                Map.of("id", idOrganizacao),
                (rs, rowNum) -> mapOrganizacao(rs));
        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("Organizacao nao encontrada");
        }
        return lista.get(0);
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
                returning id_organizacao, nm_organizacao, ds_documento, slug, database_name, status, fl_ativo, dt_criacao
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

    public OrganizacaoResponseDTO atualizar(Long idOrganizacao, OrganizacaoRequestDTO request) {
        buscar(idOrganizacao);
        return jdbcTemplate.queryForObject("""
                update organizacao set
                    nm_organizacao = :nome,
                    ds_documento = :documento,
                    dt_atualizacao = now()
                where id_organizacao = :id
                returning id_organizacao, nm_organizacao, ds_documento, slug, database_name, status, fl_ativo, dt_criacao
                """,
                Map.of(
                        "id", idOrganizacao,
                        "nome", request.nmOrganizacao(),
                        "documento", request.dsDocumento()),
                (rs, rowNum) -> mapOrganizacao(rs));
    }

    public void excluir(Long idOrganizacao) {
        buscar(idOrganizacao);
        jdbcTemplate.update("""
                update organizacao set fl_ativo = false, dt_atualizacao = now()
                where id_organizacao = :id
                """,
                Map.of("id", idOrganizacao));
    }

    public List<VinculoUsuarioResponseDTO> listarVinculos(Long idOrganizacao) {
        buscarOrganizacao(idOrganizacao);
        return jdbcTemplate.query("""
                select ug.id_usuario, ug.nu_cpf, ug.nm_usuario, uo.ds_role
                from usuario_organizacao uo
                join usuario_global ug on ug.id_usuario = uo.id_usuario
                where uo.id_organizacao = :idOrganizacao
                  and uo.fl_ativo = true
                  and ug.fl_ativo = true
                order by ug.nm_usuario
                """,
                Map.of("idOrganizacao", idOrganizacao),
                (rs, rowNum) -> new VinculoUsuarioResponseDTO(
                        rs.getLong("id_usuario"),
                        rs.getString("nu_cpf"),
                        rs.getString("nm_usuario"),
                        rs.getString("ds_role")));
    }

    public void vincularUsuario(Long idOrganizacao, VinculoUsuarioRequestDTO request) {
        buscarOrganizacao(idOrganizacao);

        List<Long> existentes = jdbcTemplate.query("""
                select id_usuario from usuario_global where nu_cpf = :cpf
                """,
                Map.of("cpf", request.nuCpf()),
                (rs, rowNum) -> rs.getLong("id_usuario"));

        Long idUsuario;
        if (existentes.isEmpty()) {
            idUsuario = jdbcTemplate.queryForObject("""
                    insert into usuario_global (nu_cpf, nm_usuario, ds_senha, tp_global, fl_ativo)
                    values (:cpf, :nome, :senha, 'DEFAULT', true)
                    returning id_usuario
                    """,
                    Map.of(
                            "cpf", request.nuCpf(),
                            "nome", request.nmUsuario(),
                            "senha", passwordEncoder.encode(request.dsSenha())),
                    Long.class);
        } else {
            idUsuario = existentes.get(0);
            atualizarDadosUsuarioGlobal(idUsuario, request.nmUsuario(), request.dsSenha());
        }

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

    public void atualizarVinculo(Long idOrganizacao, Long idUsuario, VinculoUsuarioUpdateDTO request) {
        buscarOrganizacao(idOrganizacao);
        validarVinculoAtivo(idOrganizacao, idUsuario);

        atualizarDadosUsuarioGlobal(idUsuario, request.nmUsuario(), request.dsSenha());

        jdbcTemplate.update("""
                update usuario_organizacao set ds_role = :role
                where id_usuario = :idUsuario and id_organizacao = :idOrganizacao and fl_ativo = true
                """,
                Map.of(
                        "idUsuario", idUsuario,
                        "idOrganizacao", idOrganizacao,
                        "role", request.dsRole()));
    }

    public void excluirVinculo(Long idOrganizacao, Long idUsuario) {
        buscarOrganizacao(idOrganizacao);
        validarVinculoAtivo(idOrganizacao, idUsuario);

        jdbcTemplate.update("""
                update usuario_organizacao set fl_ativo = false
                where id_usuario = :idUsuario and id_organizacao = :idOrganizacao
                """,
                Map.of(
                        "idUsuario", idUsuario,
                        "idOrganizacao", idOrganizacao));
    }

    private void atualizarDadosUsuarioGlobal(Long idUsuario, String nmUsuario, String dsSenha) {
        if (StringUtils.hasText(dsSenha)) {
            if (dsSenha.length() < 6) {
                throw new BusinessException("Senha deve ter no minimo 6 caracteres");
            }
            jdbcTemplate.update("""
                    update usuario_global set
                        nm_usuario = :nome,
                        ds_senha = :senha,
                        dt_atualizacao = now()
                    where id_usuario = :id
                    """,
                    Map.of(
                            "id", idUsuario,
                            "nome", nmUsuario,
                            "senha", passwordEncoder.encode(dsSenha)));
            return;
        }

        jdbcTemplate.update("""
                update usuario_global set nm_usuario = :nome, dt_atualizacao = now()
                where id_usuario = :id
                """,
                Map.of("id", idUsuario, "nome", nmUsuario));
    }

    private void validarVinculoAtivo(Long idOrganizacao, Long idUsuario) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from usuario_organizacao
                where id_organizacao = :idOrganizacao
                  and id_usuario = :idUsuario
                  and fl_ativo = true
                """,
                Map.of("idOrganizacao", idOrganizacao, "idUsuario", idUsuario),
                Integer.class);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Vinculo de usuario nao encontrado");
        }
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
                rs.getString("ds_documento"),
                rs.getString("slug"),
                rs.getString("database_name"),
                rs.getString("status"),
                rs.getBoolean("fl_ativo"),
                rs.getObject("dt_criacao", java.time.LocalDateTime.class));
    }
}
