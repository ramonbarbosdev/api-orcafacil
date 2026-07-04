package com.api_orcafacil.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.ChaveLimite;

/**
 * Efeitos no banco central após commit do tenant — agrupados em uma única transação central
 * para evitar estado parcial (consumo registrado sem mapeamento público, ou vice-versa).
 */
@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrcamentoCentralSyncService {

    private final PoliticaPlanoService politicaPlanoService;
    private final OrcamentoPublicoService orcamentoPublicoService;

    public OrcamentoCentralSyncService(
            PoliticaPlanoService politicaPlanoService,
            OrcamentoPublicoService orcamentoPublicoService) {
        this.politicaPlanoService = politicaPlanoService;
        this.orcamentoPublicoService = orcamentoPublicoService;
    }

    @Transactional(transactionManager = "centralTransactionManager", rollbackFor = Exception.class)
    public void aplicarPosCommitSalvar(Long idOrganizacao, String cdPublico, Long idOrcamento, boolean novo) {
        if (novo) {
            politicaPlanoService.registrarConsumo(idOrganizacao, ChaveLimite.ORCAMENTOS_MES, 1);
        }
        if (cdPublico != null && !cdPublico.isBlank()) {
            orcamentoPublicoService.registrar(cdPublico, idOrganizacao, idOrcamento);
        }
    }

    @Transactional(transactionManager = "centralTransactionManager", rollbackFor = Exception.class)
    public void aplicarPosCommitExcluir(
            String cdPublico, Long idOrganizacao, Long idOrcamento, boolean consumoMesAtual) {
        orcamentoPublicoService.excluir(cdPublico, idOrganizacao, idOrcamento);
        if (consumoMesAtual) {
            politicaPlanoService.registrarConsumo(idOrganizacao, ChaveLimite.ORCAMENTOS_MES, -1);
        }
    }
}
