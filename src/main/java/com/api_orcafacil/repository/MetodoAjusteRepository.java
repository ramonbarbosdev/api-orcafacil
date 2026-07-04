package com.api_orcafacil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.MetodoAjuste;

public interface MetodoAjusteRepository extends TenantRepository<MetodoAjuste> {

    Optional<MetodoAjuste> findByIdMetodoAjusteAndIdOrganizacao(Long id, Long idOrganizacao);

    List<MetodoAjuste> findByIdOrganizacao(Long idOrganizacao);

    @Query(value = "SELECT * FROM metodo_ajustes b WHERE b.id_campopersonalizado = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<MetodoAjuste> findByIdCampoPersonalizadoAndIdOrganizacao(Long idCampoPersonalizado, Long idOrganizacao);
}
