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

@Service
public class OrcamentoItemCampoValorService {

    @Autowired
    private OrcamentoItemCampoValorRepository repository;

  
    public void salvar(List<OrcamentoItemCampoValor>  objeto, OrcamentoItem itemSalvo) throws Exception {

        Function<OrcamentoItemCampoValor, Long> getIdFunction = OrcamentoItemCampoValor::getIdOrcamentoItemCampoValor;

        if (objeto != null) {

            MestreDetalheUtils.removerItensGenerico(
                    itemSalvo.getIdOrcamentoItem(),
                    objeto,
                    repository::findbyIdMestre,
                    repository::deleteById,
                    getIdFunction);

            for (OrcamentoItemCampoValor campo : objeto) {

                campo.setIdOrcamentoItem(itemSalvo.getIdOrcamentoItem());

                if (campo.getIdOrcamentoItemCampoValor() == null
                        || campo.getIdOrcamentoItemCampoValor() == 0) {

                    campo.setIdOrcamentoItemCampoValor(null);
                }

                repository.save(campo);
            }
        }
    }

    public void validarObjeto(OrcamentoItemCampoValor objeto) throws Exception {

    }

}
