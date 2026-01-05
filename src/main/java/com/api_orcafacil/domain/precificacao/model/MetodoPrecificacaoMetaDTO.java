package com.api_orcafacil.domain.precificacao.model;

import java.util.List;

import com.api_orcafacil.enums.TipoPrecificacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPrecificacaoMetaDTO {

    private Long idMetodoPrecificacao;
    private TipoPrecificacao cdMetodoPrecificacao;
    private String nmMetodoPrecificacao;
    private String dsMetodoPrecificacao;

    private List<CampoMetodoDTO> campos;
}
