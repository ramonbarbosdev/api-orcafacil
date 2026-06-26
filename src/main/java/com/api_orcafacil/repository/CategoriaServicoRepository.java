package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CategoriaServico;

public interface CategoriaServicoRepository extends TenantRepository<CategoriaServico> {

    Optional<CategoriaServico> findByIdCategoriaServicoAndIdOrganizacao(Long id, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(c.cd_categoriaservico AS BIGINT)), 0) FROM categoria_servico c WHERE c.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM categoria_servico b WHERE b.cd_categoriaservico = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<CategoriaServico> findByCdCategoriaServicoAndIdOrganizacao(String cd, Long idOrganizacao);
}
