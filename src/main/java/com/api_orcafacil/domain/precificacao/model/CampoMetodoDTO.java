package com.api_orcafacil.domain.precificacao.model;

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
    private String tipo;       // NUMBER | TEXT | BOOLEAN
    private boolean obrigatorio;
}