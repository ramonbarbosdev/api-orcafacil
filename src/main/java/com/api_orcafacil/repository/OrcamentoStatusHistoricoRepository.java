package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.OrcamentoStatusHistorico;

public interface OrcamentoStatusHistoricoRepository extends TenantRepository<OrcamentoStatusHistorico> {

    List<OrcamentoStatusHistorico> findByIdOrcamentoOrderByDtCriacaoAsc(Long idOrcamento);

    @Modifying
    @Query("DELETE FROM OrcamentoStatusHistorico h WHERE h.idOrcamento = :idOrcamento")
    void deleteByIdOrcamento(Long idOrcamento);
}
