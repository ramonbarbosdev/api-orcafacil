package com.api_orcafacil.domain.precificacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CampoPersonalizadoRepository extends JpaRepository<CampoPersonalizado, Long> {

        List<CampoPersonalizado> findAllByIdTenant(String idTenant);

        @Query(value = "SELECT *  FROM campos_personalizados b WHERE b.cd_campopersonalizado = ?1 and b.id_tenant = ?2 limit 1  ", nativeQuery = true)
        Optional<CampoPersonalizado> verificarCodigoExistente(String cd_campopersonalizado, String idTenant);

}
