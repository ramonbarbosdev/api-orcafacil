package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Catalogo;

public interface CatalogoRepository extends TenantRepository<Catalogo> {

    long countByIdOrganizacao(Long idOrganizacao);

    Optional<Catalogo> findByIdCatalogoAndIdOrganizacao(Long idCatalogo, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(c.cd_catalogo AS BIGINT)), 0) FROM catalogo c WHERE c.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM catalogo b WHERE b.cd_catalogo = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Catalogo> findByCdCatalogoAndIdOrganizacao(String cdCatalogo, Long idOrganizacao);
}
