package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CampoPersonalizado;

public interface CampoPersonalizadoRepository extends TenantRepository<CampoPersonalizado> {

    Optional<CampoPersonalizado> findByIdCampoPersonalizadoAndIdOrganizacao(Long id, Long idOrganizacao);

    @Query(value = "SELECT * FROM campos_personalizados b WHERE b.cd_campopersonalizado = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<CampoPersonalizado> findByCdCampoPersonalizadoAndIdOrganizacao(String cd, Long idOrganizacao);
}
