package com.api_orcafacil.domain.precificacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;
import com.vladmihalcea.spring.repository.BaseJpaRepository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface EmpresaMetodoPrecificacaoRepository extends BaseRepository<EmpresaMetodoPrecificacao, Long> {

        Optional<EmpresaMetodoPrecificacao> findByIdTenant(String idTenant);

        @Query(value = "SELECT *  FROM empresa_metodo_precificacao b WHERE b.id_metodoprecificacao = ?1 and b.id_tenant = ?2 limit 1  ", nativeQuery = true)
        Optional<EmpresaMetodoPrecificacao> verificarCodigoExistente(Long idMetodoPrecificacao, String idTenant);

}
