package com.api_orcafacil.domain.precificacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.enums.TipoPrecificacao;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface MetodoPrecificacaoRepository extends JpaRepository<MetodoPrecificacao, Long> {

        @Query(value = "SELECT *  FROM metodo_precificacao b WHERE b.cd_metodoprecificacao = ?1 limit 1  ", nativeQuery = true)
        Optional<MetodoPrecificacao> verificarCodigoExistente(String codigo);

        Optional <MetodoPrecificacao> findByCdMetodoPrecificacao(TipoPrecificacao nome);
}
