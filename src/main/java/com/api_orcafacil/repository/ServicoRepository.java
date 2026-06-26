package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Servico;

public interface ServicoRepository extends TenantRepository<Servico> {

    Optional<Servico> findByIdServicoAndIdOrganizacao(Long idServico, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(s.cd_servico AS BIGINT)), 0) FROM servico s WHERE s.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM servico b WHERE b.cd_servico = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Servico> findByCdServicoAndIdOrganizacao(String cd, Long idOrganizacao);

    long countByIdOrganizacao(Long idOrganizacao);
}
