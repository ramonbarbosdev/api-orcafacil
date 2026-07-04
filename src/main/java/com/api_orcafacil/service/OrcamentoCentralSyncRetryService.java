package com.api_orcafacil.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.repository.central.CentralOrcamentoSyncPendenteRepository;
import com.api_orcafacil.tenant.central.model.CentralOrcamentoSyncPendente;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrcamentoCentralSyncRetryService {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoCentralSyncRetryService.class);
    private static final int MAX_TENTATIVAS = 5;
    private static final long[] BACKOFF_SEGUNDOS = { 30, 120, 300, 900, 1800 };

    private final CentralOrcamentoSyncPendenteRepository pendenteRepository;
    private final OrcamentoCentralSyncService syncService;

    public OrcamentoCentralSyncRetryService(
            CentralOrcamentoSyncPendenteRepository pendenteRepository,
            OrcamentoCentralSyncService syncService) {
        this.pendenteRepository = pendenteRepository;
        this.syncService = syncService;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void agendarSalvar(Long idOrganizacao, String cdPublico, Long idOrcamento, boolean novo) {
        CentralOrcamentoSyncPendente pendente = new CentralOrcamentoSyncPendente();
        pendente.setIdOrganizacao(idOrganizacao);
        pendente.setIdOrcamento(idOrcamento);
        pendente.setCdPublico(cdPublico);
        pendente.setTpOperacao("SALVAR");
        pendente.setFlNovo(novo);
        pendente.setNuTentativas(0);
        pendente.setDtProximoRetry(LocalDateTime.now().plusSeconds(BACKOFF_SEGUNDOS[0]));
        pendenteRepository.save(pendente);
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void agendarExcluir(String cdPublico, Long idOrganizacao, Long idOrcamento, boolean consumoMesAtual) {
        CentralOrcamentoSyncPendente pendente = new CentralOrcamentoSyncPendente();
        pendente.setIdOrganizacao(idOrganizacao);
        pendente.setIdOrcamento(idOrcamento);
        pendente.setCdPublico(cdPublico);
        pendente.setTpOperacao(consumoMesAtual ? "EXCLUIR_COM_CONSUMO" : "EXCLUIR");
        pendente.setNuTentativas(0);
        pendente.setDtProximoRetry(LocalDateTime.now().plusSeconds(BACKOFF_SEGUNDOS[0]));
        pendenteRepository.save(pendente);
    }

    @Scheduled(fixedDelayString = "${app.saas.central.sync-retry-interval-ms:60000}")
    public void processarPendentes() {
        List<CentralOrcamentoSyncPendente> pendentes = pendenteRepository
                .findTop20ByNuTentativasLessThanAndDtProximoRetryBeforeOrderByDtProximoRetryAsc(
                        MAX_TENTATIVAS, LocalDateTime.now());
        for (CentralOrcamentoSyncPendente pendente : pendentes) {
            processarUm(pendente);
        }
    }

    @Transactional(transactionManager = "centralTransactionManager")
    void processarUm(CentralOrcamentoSyncPendente pendente) {
        try {
            switch (pendente.getTpOperacao()) {
                case "SALVAR" -> syncService.aplicarPosCommitSalvar(
                        pendente.getIdOrganizacao(),
                        pendente.getCdPublico(),
                        pendente.getIdOrcamento(),
                        pendente.isFlNovo());
                case "EXCLUIR" -> syncService.aplicarPosCommitExcluir(
                        pendente.getCdPublico(),
                        pendente.getIdOrganizacao(),
                        pendente.getIdOrcamento(),
                        false);
                case "EXCLUIR_COM_CONSUMO" -> syncService.aplicarPosCommitExcluir(
                        pendente.getCdPublico(),
                        pendente.getIdOrganizacao(),
                        pendente.getIdOrcamento(),
                        true);
                default -> log.warn("Operacao de sync desconhecida: {}", pendente.getTpOperacao());
            }
            pendenteRepository.delete(pendente);
        } catch (Exception ex) {
            int tentativa = pendente.getNuTentativas() + 1;
            pendente.setNuTentativas(tentativa);
            pendente.setDsErro(ex.getMessage());
            if (tentativa >= MAX_TENTATIVAS) {
                log.error(
                        "Sync central do orcamento {} esgotou tentativas ({}): {}",
                        pendente.getIdOrcamento(),
                        pendente.getTpOperacao(),
                        ex.getMessage());
            } else {
                long segundos = BACKOFF_SEGUNDOS[Math.min(tentativa, BACKOFF_SEGUNDOS.length - 1)];
                pendente.setDtProximoRetry(LocalDateTime.now().plusSeconds(segundos));
                log.warn(
                        "Falha no retry sync central orcamento {} (tentativa {}): {}",
                        pendente.getIdOrcamento(),
                        tentativa,
                        ex.getMessage());
            }
            pendenteRepository.save(pendente);
        }
    }
}
