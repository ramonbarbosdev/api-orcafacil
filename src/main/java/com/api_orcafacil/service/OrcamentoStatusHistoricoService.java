package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoStatusHistorico;
import com.api_orcafacil.repository.OrcamentoStatusHistoricoRepository;

@Service
public class OrcamentoStatusHistoricoService {

    private final OrcamentoStatusHistoricoRepository repository;
    private final TenantContextService tenantContextService;

    public OrcamentoStatusHistoricoService(OrcamentoStatusHistoricoRepository repository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional
    public void registrar(Orcamento orcamento, StatusOrcamento statusAnterior, StatusOrcamento statusNovo) {
        OrcamentoStatusHistorico historico = new OrcamentoStatusHistorico();
        historico.setIdOrganizacao(orcamento.getIdOrganizacao());
        historico.setIdOrcamento(orcamento.getIdOrcamento());
        historico.setOrcamento(orcamento);
        historico.setTpStatusAnterior(statusAnterior);
        historico.setTpStatusNovo(statusNovo);
        repository.save(historico);
    }

    public List<OrcamentoStatusHistorico> listarPorOrcamento(Long idOrcamento) {
        return repository.findByIdOrcamentoOrderByDtCriacaoAsc(idOrcamento);
    }

    @Transactional
    public void excluirPorIdOrcamento(Long idOrcamento) {
        repository.deleteByIdOrcamento(idOrcamento);
    }
}
