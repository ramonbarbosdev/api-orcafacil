package com.api_orcafacil.domain.precificacao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.precificacao.model.CampoMetodoDTO;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacaoMetaDTO;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;
import com.api_orcafacil.enums.TipoPrecificacao;

@Service
public class MetodoPrecificacaoService {

    @Autowired
    private MetodoPrecificacaoRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public MetodoPrecificacao salvar(MetodoPrecificacao objeto) throws Exception {

        objeto = repository.save(objeto);

        return objeto;
    }

    public MetodoPrecificacaoMetaDTO montar(MetodoPrecificacao metodo) {

        MetodoPrecificacaoMetaDTO dto = new MetodoPrecificacaoMetaDTO();
        dto.setIdMetodoPrecificacao(metodo.getIdMetodoPrecificacao());
        dto.setCdMetodoPrecificacao(metodo.getCdMetodoPrecificacao());
        dto.setNmMetodoPrecificacao(metodo.getNmMetodoPrecificacao());
        dto.setDsMetodoPrecificacao(metodo.getDsMetodoPrecificacao());

        dto.setCampos(obterCamposPorTipo(metodo.getCdMetodoPrecificacao()));

        return dto;
    }

    private List<CampoMetodoDTO> obterCamposPorTipo(TipoPrecificacao tipo) {

        return switch (tipo) {

            case MARKUP -> List.of(
                    new CampoMetodoDTO(
                            "percentual",
                            "Percentual de Markup",
                            "NUMBER",
                            true));

            case MARGEM -> List.of(
                    new CampoMetodoDTO(
                            "percentual",
                            "Percentual de Margem",
                            "NUMBER",
                            true));

            case FIXO -> List.of(
                    new CampoMetodoDTO(
                            "valor",
                            "Valor Fixo",
                            "NUMBER",
                            true));

            case SIMPLES -> List.of(); 
        };
    }

}
