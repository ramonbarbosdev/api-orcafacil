package com.api_orcafacil.dto.orcamento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteVisualizacaoDTO {

    private Long idCliente;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
}
