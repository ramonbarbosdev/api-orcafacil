package com.api_orcafacil.repository.central;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralOrcamentoPublico;

public interface CentralOrcamentoPublicoRepository extends JpaRepository<CentralOrcamentoPublico, String> {

    void deleteByIdOrganizacaoAndIdOrcamento(Long idOrganizacao, Long idOrcamento);
}
