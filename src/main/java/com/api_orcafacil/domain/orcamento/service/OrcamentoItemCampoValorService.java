package com.api_orcafacil.domain.orcamento.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemCampoValorRepository;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.MestreDetalheUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class OrcamentoItemCampoValorService {

    @Autowired
    private OrcamentoItemCampoValorRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void salvar(List<OrcamentoItemCampoValor> campos,
            OrcamentoItem itemSalvo) {

        if (campos == null || campos.isEmpty())
            return;

        OrcamentoItem itemManaged = entityManager.find(
                OrcamentoItem.class,
                itemSalvo.getIdOrcamentoItem());

        MestreDetalheUtils.removerItensGenerico(
                itemManaged.getIdOrcamentoItem(),
                campos,
                repository::findbyIdMestre,
                repository::deleteById,
                OrcamentoItemCampoValor::getIdOrcamentoItemCampoValor);

        for (OrcamentoItemCampoValor campo : campos) {

            campo.setOrcamentoItem(itemManaged); // 🔴 chave

            if (campo.getIdOrcamentoItemCampoValor() == null
                    || campo.getIdOrcamentoItemCampoValor() == 0) {
                campo.setIdOrcamentoItemCampoValor(null);
            }

            repository.save(campo); // merge
        }
    }

    public void validarObjeto(OrcamentoItemCampoValor objeto) throws Exception {

    }

    public void excluirPorMestre(Long idMestre) throws Exception {

        repository.deleteByIdMestre(idMestre);

    }

}
