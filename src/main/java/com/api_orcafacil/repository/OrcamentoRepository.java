package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Orcamento;

public interface OrcamentoRepository extends TenantRepository<Orcamento> {

    Optional<Orcamento> findByIdOrcamentoAndIdOrganizacao(Long idOrcamento, Long idOrganizacao);

    Optional<Orcamento> findByCdPublico(String cdPublico);

    @Query(value = """
            SELECT COALESCE(
              MAX(CAST(SUBSTRING(c.nu_orcamento FROM '[0-9]+') AS BIGINT)),
              0
            )
            FROM orcamento c
            WHERE c.id_organizacao = ?1
            """, nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM orcamento b WHERE b.nu_orcamento = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Orcamento> findByNuOrcamentoAndIdOrganizacao(String nuOrcamento, Long idOrganizacao);
}
