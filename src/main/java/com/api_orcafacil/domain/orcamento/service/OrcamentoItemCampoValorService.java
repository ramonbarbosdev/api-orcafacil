package com.api_orcafacil.domain.orcamento.service;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<OrcamentoItemCampoValor, Long> ID_FUNCTION = OrcamentoItemCampoValor::getIdOrcamentoItemCampoValor;

    public void validarObjeto(OrcamentoItemCampoValor objeto, List<OrcamentoItemCampoValor> itens) throws Exception {
        Function<OrcamentoItemCampoValor, Long> getIdFunction = OrcamentoItemCampoValor::getIdOrcamentoItemCampoValor;

        Long idExistente = getIdFunction.apply(objeto);

        boolean isCodigoExiste = itens.stream()
                .filter(i -> !Objects.equals(i.getIdOrcamentoItemCampoValor(), idExistente))
                .anyMatch(i -> i.getIdCampoPersonalizado().equals(objeto.getIdCampoPersonalizado()));

        if (isCodigoExiste) {
            throw new Exception(
                    "Não foi possivel salvar, existe materiais repetidos nos ajustes. Verifique a listagem ou utilize um novo material.");
        }

    }

    public void excluirPorMestre(Long idMestre) throws Exception {

        repository.deleteByIdMestre(idMestre);

    }

}
