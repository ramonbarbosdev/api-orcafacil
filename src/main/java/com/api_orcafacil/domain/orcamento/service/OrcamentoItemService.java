package com.api_orcafacil.domain.orcamento.service;

import java.util.ArrayList;
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
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemCampoValorRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoItemRepository;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.MestreDetalheUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class OrcamentoItemService {

    @Autowired
    private OrcamentoItemRepository repository;

    @Autowired
    private OrcamentoItemCampoValorService orcamentoItemCampoValorService;

    // public void salvar(Orcamento objeto,
    //         List<OrcamentoItem> itens) throws Exception {

    //     Function<Orcamento, Long> getIdFunctionMestre = Orcamento::getIdOrcamento;
    //     Function<OrcamentoItem, Long> getIdFunction = OrcamentoItem::getIdOrcamentoItem;

    //     Long idMestre = getIdFunctionMestre.apply(objeto);

    //     if (itens == null || itens.isEmpty()) {

    //         excluirPorMestre(idMestre);

    //         if (objeto.getOrcamentoItem() != null) {
    //             for (OrcamentoItem antigo : objeto.getOrcamentoItem()) {
    //                 antigo.setOrcamento(null);
    //                 antigo.setIdOrcamento(null);
    //             }
    //         }

    //         objeto.setOrcamentoItem(new ArrayList<>());
    //         return;
    //     }

    //     MestreDetalheUtils.removerItensGenerico(
    //             idMestre,
    //             itens,
    //             repository::findbyIdMestre,
    //             repository::deleteById,
    //             getIdFunction,
    //             itemRemovido -> {
    //                 itemRemovido.setOrcamento(null);
    //                 itemRemovido.setIdOrcamento(null);
    //             });


    //     if (itens != null && itens.size() > 0) {
    //         for (OrcamentoItem item : itens) {
    //             item.setIdOrcamento(idMestre);

    //             Long idExistente = getIdFunction.apply(item);

    //             if (idExistente == null || idExistente == 0) {
    //                 item.setIdOrcamentoItem(null);
    //             }

    //             OrcamentoItem itemSalvo = repository.save(item);

    //             List<OrcamentoItemCampoValor> campos = item.getOrcamentoItemCampoValor();

    //             orcamentoItemCampoValorService.salvar(campos, itemSalvo);

    //         }

    //         objeto.setOrcamentoItem(itens);
    //     }
    // }

    public void validarObjeto(OrcamentoItemCampoValor objeto) throws Exception {

    }

    @PersistenceContext
    private EntityManager entityManager;

    public void excluirPorMestre(Long idMestre) throws Exception {

        List<OrcamentoItem> objetos = repository.findbyIdMestre(idMestre);

        for (OrcamentoItem orcamentoItem : objetos) {
            orcamentoItemCampoValorService.excluirPorMestre(orcamentoItem.getIdOrcamentoItem());

        }

        repository.deleteByIdMestre(idMestre);
    }

    @Transactional(rollbackFor = Exception.class)
    public void excluir(Long id) throws Exception {

        repository.deleteById(id);
    }

}
