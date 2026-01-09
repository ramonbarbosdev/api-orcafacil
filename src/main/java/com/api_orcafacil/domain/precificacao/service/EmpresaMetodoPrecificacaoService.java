package com.api_orcafacil.domain.precificacao.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;

@Service
public class EmpresaMetodoPrecificacaoService {

    @Autowired
    private EmpresaMetodoPrecificacaoRepository repository;

    @Autowired
    private MetodoPrecificacaoRepository metodoPrecificacaoRepository;

    @Transactional(rollbackFor = Exception.class)
    public EmpresaMetodoPrecificacao salvar(
            String idTenant,
            EmpresaMetodoPrecificacao dados) {

        EmpresaMetodoPrecificacao atual = obterOuCriarPadrao(idTenant);

        atual.setIdMetodoPrecificacao(dados.getIdMetodoPrecificacao());

        return repository.save(atual);
    }

    public EmpresaMetodoPrecificacao obterOuCriarPadrao(String idTenant) {

        EmpresaMetodoPrecificacao objeto = repository.findByIdTenant(idTenant)
                .orElseGet(() -> {

                    MetodoPrecificacao metodo = metodoPrecificacaoRepository.findByCdMetodoPrecificacao("SIMPLES")
                            .orElseThrow(() -> new IllegalStateException(
                                    "Método de precificação padrão não cadastrado"));

                    EmpresaMetodoPrecificacao cfg = new EmpresaMetodoPrecificacao();
                    cfg.setIdTenant(idTenant);
                    cfg.setIdMetodoPrecificacao(metodo.getIdMetodoPrecificacao());
                    cfg.setConfiguracao(new HashMap<>());

                    return repository.save(cfg);
                });

        return objeto;
    }

}
