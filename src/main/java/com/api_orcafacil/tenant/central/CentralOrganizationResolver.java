package com.api_orcafacil.tenant.central;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.api_orcafacil.tenant.OrganizationResolver;
import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.StorageMode;
import com.api_orcafacil.tenant.TenantDescriptor;

@Component
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class CentralOrganizationResolver implements OrganizationResolver {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CentralOrganizationResolver(
            @Qualifier("centralNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TenantDescriptor resolver(Long idOrganizacao) {
        try {
            return jdbcTemplate.queryForObject("""
                    select
                        id_organizacao,
                        slug,
                        storage_mode,
                        database_name,
                        database_host_ref,
                        database_port,
                        status
                    from organizacao
                    where id_organizacao = :idOrganizacao
                      and fl_ativo = true
                      and status = 'ATIVA'
                    """,
                    Map.of("idOrganizacao", idOrganizacao),
                    (rs, rowNum) -> new TenantDescriptor(
                            rs.getLong("id_organizacao"),
                            rs.getString("slug"),
                            StorageMode.valueOf(rs.getString("storage_mode")),
                            rs.getString("database_name"),
                            rs.getString("database_host_ref"),
                            (Integer) rs.getObject("database_port"),
                            OrganizationStatus.valueOf(rs.getString("status"))));
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalStateException(
                    "Organizacao ativa nao encontrada no banco central: " + idOrganizacao, ex);
        }
    }
}
