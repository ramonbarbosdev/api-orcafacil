package com.api_orcafacil.dto.orcamento;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoPreviewPrecificacaoRequest {

    private Long idOrcamento;
    private Long idEmpresaMetodoPrecificacao;

    @NotEmpty(message = "O orcamento deve possuir ao menos um item")
    @Valid
    private List<OrcamentoItemRequest> itens;
}
