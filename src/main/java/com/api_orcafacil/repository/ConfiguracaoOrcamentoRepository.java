package com.api_orcafacil.repository;

import java.util.Optional;

import com.api_orcafacil.model.ConfiguracaoOrcamento;

public interface ConfiguracaoOrcamentoRepository extends TenantRepository<ConfiguracaoOrcamento> {

    Optional<ConfiguracaoOrcamento> findFirstByIdOrganizacao(Long idOrganizacao);
}
