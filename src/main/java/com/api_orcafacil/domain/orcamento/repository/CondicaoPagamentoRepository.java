package com.api_orcafacil.domain.orcamento.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

public interface CondicaoPagamentoRepository extends BaseRepository<CodicaoPagamento, Long> {

    Optional<CodicaoPagamento> findByIdTenant(String idTenant);

    @Query(value = "SELECT CASE WHEN MAX(c.cd_codicaopagamento) IS NULL THEN '0' ELSE MAX(c.cd_codicaopagamento) END FROM codicao_pagamento c ", nativeQuery = true)
    Long obterSequencial();

    @Query(value = "SELECT *  FROM codicao_pagamento b WHERE b.cd_codicaopagamento = ?1 limit 1  ", nativeQuery = true)
    Optional<CodicaoPagamento> verificarCodigoExistente(String codigo);

}