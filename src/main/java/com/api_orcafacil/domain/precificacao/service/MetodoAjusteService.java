package com.api_orcafacil.domain.precificacao.service;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoAjuste;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoAjusteRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;

@Service
public class MetodoAjusteService {

    @Autowired
    private MetodoAjusteRepository repository;

    @Autowired
    private EmpresaMetodoPrecificacaoRepository empresaMetodoPrecificacaoRepository;

    @Transactional(rollbackFor = Exception.class)
    public MetodoAjuste salvar(MetodoAjuste objeto) {

        Optional<EmpresaMetodoPrecificacao> empresaMetodo = Optional.ofNullable(empresaMetodoPrecificacaoRepository
                .findByIdTenant(objeto.getIdTenant())
                .orElseThrow(() -> new IllegalStateException(
                        "A Empresa Método de precificação não encontrada")));

        objeto.setIdEmpresaMetodoPrecificacao(empresaMetodo.get().getIdEmpresaMetodoPrecificacao());

        return repository.save(objeto);
    }

}
