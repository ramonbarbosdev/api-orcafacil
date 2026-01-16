package com.api_orcafacil.domain.orcamento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteVisualizacaoDTO {
    
    private Long idCliente;
    private String nome;
    private String email;
    private String telefone;
}
