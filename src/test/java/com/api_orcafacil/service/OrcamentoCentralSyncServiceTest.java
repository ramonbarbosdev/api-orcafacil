package com.api_orcafacil.service;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api_orcafacil.common.ChaveLimite;

@ExtendWith(MockitoExtension.class)
class OrcamentoCentralSyncServiceTest {

    @Mock
    private PoliticaPlanoService politicaPlanoService;
    @Mock
    private OrcamentoPublicoService orcamentoPublicoService;

    @InjectMocks
    private OrcamentoCentralSyncService service;

    @Test
    void aplicarPosCommitSalvo_registraConsumoEMapeamentoPublico() {
        service.aplicarPosCommitSalvar(1L, "cd-abc", 99L, true);

        InOrder ordem = inOrder(politicaPlanoService, orcamentoPublicoService);
        ordem.verify(politicaPlanoService).registrarConsumo(1L, ChaveLimite.ORCAMENTOS_MES, 1);
        ordem.verify(orcamentoPublicoService).registrar("cd-abc", 1L, 99L);
    }

    @Test
    void aplicarPosCommitExcluir_removePublicoEDecrementaConsumo() {
        service.aplicarPosCommitExcluir("cd-abc", 1L, 99L, true);

        InOrder ordem = inOrder(orcamentoPublicoService, politicaPlanoService);
        ordem.verify(orcamentoPublicoService).excluir("cd-abc", 1L, 99L);
        ordem.verify(politicaPlanoService).registrarConsumo(1L, ChaveLimite.ORCAMENTOS_MES, -1);
    }

    @Test
    void aplicarPosCommitSalvo_edicaoNaoIncrementaConsumo() {
        service.aplicarPosCommitSalvar(1L, "cd-abc", 99L, false);

        verify(politicaPlanoService, never()).registrarConsumo(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyLong());
        verify(orcamentoPublicoService).registrar("cd-abc", 1L, 99L);
    }
}
