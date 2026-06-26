package com.api_orcafacil.provisioning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.saas.provisioning.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseTenantProvisioningService implements TenantProvisioningService {

    private final NamedParameterJdbcTemplate centralJdbcTemplate;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String adminDatabase;
    private final String migrationsLocation;

    public DatabaseTenantProvisioningService(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate centralJdbcTemplate,
            @Value("${app.saas.tenant-db.host:localhost}") String host,
            @Value("${app.saas.tenant-db.port:5432}") int port,
            @Value("${app.saas.tenant-db.username:postgres}") String username,
            @Value("${app.saas.tenant-db.password:postgres}") String password,
            @Value("${app.saas.provisioning.admin-database:postgres}") String adminDatabase,
            @Value("${app.saas.provisioning.migrations-location:classpath:db/migration}") String migrationsLocation) {
        this.centralJdbcTemplate = centralJdbcTemplate;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.adminDatabase = adminDatabase;
        this.migrationsLocation = migrationsLocation;
    }

    @Override
    public void provisionar(TenantProvisioningPlan plan) {
        validarDatabaseName(plan.databaseName());
        Long idProvisionamento = iniciarProvisionamento(plan);
        try {
            atualizarEtapa(idProvisionamento, "CRIANDO_DATABASE");
            criarDatabaseSeNaoExiste(plan.databaseName());
            atualizarEtapa(idProvisionamento, "EXECUTANDO_MIGRATIONS");
            executarMigrations(plan.databaseName());
            concluirProvisionamento(idProvisionamento, plan);
        } catch (RuntimeException ex) {
            falharProvisionamento(idProvisionamento, plan, ex);
            throw ex;
        }
    }

    private Long iniciarProvisionamento(TenantProvisioningPlan plan) {
        return centralJdbcTemplate.queryForObject("""
                insert into provisionamento_tenant (
                    id_organizacao, status, etapa_atual, database_name, tentativas, dt_inicio
                )
                values (:idOrganizacao, 'EM_EXECUCAO', 'INICIANDO', :databaseName, 1, now())
                returning id_provisionamento
                """,
                Map.of(
                        "idOrganizacao", plan.idOrganizacao(),
                        "databaseName", plan.databaseName()),
                Long.class);
    }

    private void atualizarEtapa(Long idProvisionamento, String etapa) {
        centralJdbcTemplate.update("""
                update provisionamento_tenant set etapa_atual = :etapa where id_provisionamento = :id
                """,
                Map.of("id", idProvisionamento, "etapa", etapa));
    }

    private void concluirProvisionamento(Long idProvisionamento, TenantProvisioningPlan plan) {
        centralJdbcTemplate.update("""
                update provisionamento_tenant
                set status = 'CONCLUIDO', etapa_atual = 'CONCLUIDO', dt_fim = now()
                where id_provisionamento = :id
                """,
                Map.of("id", idProvisionamento));

        centralJdbcTemplate.update("""
                update organizacao
                set storage_mode = 'DATABASE_PER_ORG',
                    database_name = :databaseName,
                    status = 'ATIVA',
                    fl_ativo = true,
                    dt_atualizacao = now()
                where id_organizacao = :idOrganizacao
                """,
                Map.of(
                        "idOrganizacao", plan.idOrganizacao(),
                        "databaseName", plan.databaseName()));

        concederPermissoesPadrao(plan.idOrganizacao());
    }

    private void concederPermissoesPadrao(Long idOrganizacao) {
        concederPermissoesPapel(idOrganizacao, 1, null);
        concederPermissoesPapel(idOrganizacao, 2, """
                pg.nm_chave like '%.ler'
                or pg.nm_chave in ('orcamentos.criar', 'orcamentos.editar')
                or pg.nm_chave like 'perfil.%'
                """);
    }

    private void concederPermissoesPapel(Long idOrganizacao, int idPapel, String filtroPermissao) {
        String wherePermissao = filtroPermissao == null
                ? "pg.fl_ativo = true"
                : "pg.fl_ativo = true and (" + filtroPermissao + ")";
        centralJdbcTemplate.update("""
                insert into papel_permissao (id_papel, id_organizacao, id_permissao)
                select :idPapel, :idOrganizacao, pg.id_permissao
                from permissao_global pg
                where %s
                on conflict do nothing
                """.formatted(wherePermissao),
                Map.of("idPapel", idPapel, "idOrganizacao", idOrganizacao));
    }

    private void falharProvisionamento(Long idProvisionamento, TenantProvisioningPlan plan, RuntimeException ex) {
        String erro = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        centralJdbcTemplate.update("""
                update provisionamento_tenant
                set status = 'FALHOU', etapa_atual = 'FALHOU', erro = :erro, dt_fim = now()
                where id_provisionamento = :id
                """,
                Map.of("id", idProvisionamento, "erro", erro));

        centralJdbcTemplate.update("""
                update organizacao set status = 'PROVISIONAMENTO_FALHOU', dt_atualizacao = now()
                where id_organizacao = :idOrganizacao
                """,
                Map.of("idOrganizacao", plan.idOrganizacao()));
    }

    private void criarDatabaseSeNaoExiste(String databaseName) {
        String adminUrl = jdbcUrl(adminDatabase);
        try (Connection connection = DriverManager.getConnection(adminUrl, username, password);
                Statement statement = connection.createStatement()) {
            connection.setAutoCommit(true);
            try (ResultSet rs = statement.executeQuery(
                    "select 1 from pg_database where datname = '" + databaseName.replace("'", "''") + "'")) {
                if (rs.next()) {
                    return;
                }
            }
            statement.execute("create database \"" + databaseName.replace("\"", "\"\"") + "\"");
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao criar database do tenant " + databaseName, ex);
        }
    }

    private void executarMigrations(String databaseName) {
        Flyway.configure()
                .dataSource(jdbcUrl(databaseName), username, password)
                .locations(migrationsLocation)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load()
                .migrate();
    }

    private String jdbcUrl(String databaseName) {
        return "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
    }

    private void validarDatabaseName(String databaseName) {
        if (databaseName == null || !databaseName.toLowerCase(Locale.ROOT).matches("org_[a-z0-9_]+")) {
            throw new IllegalArgumentException("Nome de database de tenant invalido: " + databaseName);
        }
    }
}
