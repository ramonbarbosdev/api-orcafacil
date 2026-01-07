package com.api_orcafacil.domain.orcamento.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

public interface ConfiguracaoOrcamentoRepository
        extends JpaRepository<ConfiguracaoOrcamento, Long> {

    Optional<ConfiguracaoOrcamento> findByIdTenant(String idTenant);

    Optional<ConfiguracaoOrcamento> findFirstByIdTenantOrderByIdConfiguracaoOrcamentoAsc(String idTenant);
}