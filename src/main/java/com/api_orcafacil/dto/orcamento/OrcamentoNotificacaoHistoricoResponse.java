package com.api_orcafacil.dto.orcamento;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoNotificacaoHistoricoResponse {

    private Long idOrcamentoNotificacao;
    private String canal;
    private String destinatario;
    private boolean sucesso;
    private Long idNotificacaoExterna;
    private String erro;
    private String mensagem;
    private LocalDateTime dtCriacao;
}
