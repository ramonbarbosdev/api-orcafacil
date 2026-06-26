package com.api_orcafacil.dto.precificacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampoMetodoDTO {

    private String nome;
    private String label;
    private String tipo;
    private boolean obrigatorio;
}
