package com.api_orcafacil.repository.central;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralOrcamentoSyncPendente;

public interface CentralOrcamentoSyncPendenteRepository extends JpaRepository<CentralOrcamentoSyncPendente, Long> {

    List<CentralOrcamentoSyncPendente> findTop20ByNuTentativasLessThanAndDtProximoRetryBeforeOrderByDtProximoRetryAsc(
            int maxTentativas, LocalDateTime agora);
}
