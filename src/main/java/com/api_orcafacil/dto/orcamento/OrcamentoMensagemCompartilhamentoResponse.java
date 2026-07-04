package com.api_orcafacil.dto.orcamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoMensagemCompartilhamentoResponse {

    private String mensagem;
    private String linkOrcamento;
}
