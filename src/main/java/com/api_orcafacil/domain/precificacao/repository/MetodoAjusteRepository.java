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
import com.api_orcafacil.domain.precificacao.model.MetodoAjuste;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface MetodoAjusteRepository extends JpaRepository<MetodoAjuste, Long> {

        List<MetodoAjuste> findAllByIdTenant(String idTenant);

         @Query(value = "SELECT *  FROM metodo_ajustes b WHERE b.id_campopersonalizado = ?1 limit 1  ", nativeQuery = true)
        Optional<MetodoAjuste> verificarCampoExistente(Long codigo);

}
