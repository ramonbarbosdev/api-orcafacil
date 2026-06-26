package com.api_orcafacil.dto.cliente;

import com.api_orcafacil.common.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    private Long idCliente;
    private TipoCliente tpCliente;

    @NotBlank
    private String nuCpfcnpj;

    @NotBlank
    private String nmCliente;

    private String dsEmail;
    private String nuTelefone;
    private String nuCep;
    private String dsLogradouro;
    private String dsComplemento;
    private String dsBairro;
    private String dsCidade;
    private String dsEstado;
    private Boolean flAtivo;
    private String dsObservacoes;
}
