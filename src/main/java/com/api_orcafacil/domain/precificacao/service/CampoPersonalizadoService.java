package com.api_orcafacil.domain.precificacao.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;

@Service
public class CampoPersonalizadoService {

    @Autowired
    private CampoPersonalizadoRepository repository;



    @Transactional(rollbackFor = Exception.class)
    public CampoPersonalizado salvar(    CampoPersonalizado objeto) {

        return repository.save(objeto);
    }

   

}
