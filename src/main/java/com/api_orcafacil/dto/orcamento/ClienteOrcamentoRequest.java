package com.api_orcafacil.dto.orcamento;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteOrcamentoRequest {

    private Long idCliente;

    @NotBlank(message = "CPF/CNPJ do cliente e obrigatorio")
    private String nuCpfcnpj;

    @NotBlank(message = "Nome do cliente e obrigatorio")
    private String nmCliente;

    private String dsEmail;
    private String nuTelefone;
    private String dsObservacoes;
}
