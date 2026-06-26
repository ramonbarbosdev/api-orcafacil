package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CondicaoPagamento;

public interface CondicaoPagamentoRepository extends TenantRepository<CondicaoPagamento> {

    Optional<CondicaoPagamento> findByIdCondicaoPagamentoAndIdOrganizacao(Long id, Long idOrganizacao);

    @Query(value = "SELECT COALESCE(MAX(CAST(c.cd_codicaopagamento AS BIGINT)), 0) FROM codicao_pagamento c WHERE c.id_organizacao = ?1", nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM codicao_pagamento b WHERE b.cd_codicaopagamento = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<CondicaoPagamento> findByCdCondicaoPagamentoAndIdOrganizacao(String cd, Long idOrganizacao);
}
