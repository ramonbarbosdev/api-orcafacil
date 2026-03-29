package com.api_orcafacil.domain.precificacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CampoPersonalizadoRepository extends BaseRepository<CampoPersonalizado, Long> {

        List<CampoPersonalizado> findAllByIdTenant(String idTenant);

        @Query(value = "SELECT *  FROM campos_personalizados b WHERE b.cd_campopersonalizado = ?1 and b.id_tenant = ?2 limit 1  ", nativeQuery = true)
        Optional<CampoPersonalizado> verificarCodigoExistente(String cd_campopersonalizado, String idTenant);

}
