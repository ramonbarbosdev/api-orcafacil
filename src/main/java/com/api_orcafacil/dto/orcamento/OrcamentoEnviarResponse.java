package com.api_orcafacil.dto.orcamento;

import java.util.List;

import com.api_orcafacil.notificacao.dto.NotificacaoCanal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoEnviarResponse {

    private OrcamentoResponse orcamento;
    private List<ResultadoNotificacao> notificacoes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoNotificacao {
        private NotificacaoCanal canal;
        private String destinatario;
        private boolean sucesso;
        private Long idNotificacao;
        private String erro;
    }
}
