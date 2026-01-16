package com.api_orcafacil.domain.orcamento.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.dto.ClienteVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.dto.ItemVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.dto.MaterialVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.dto.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.dto.StatusHistoricoVisualizacaoDTO;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.orcamento.model.OrcamentoStatusHistorico;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.OrcamentoStatusHistoricoRepository;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;

@Service
public class VisualizacaoOrcamentoService {

    @Autowired
    private OrcamentoRepository repository;

    @Autowired
    private OrcamentoStatusHistoricoRepository historicoRepository;

    public OrcamentoVisualizacaoDTO visualizar(
            Long idOrcamento,
            String tenantId) {

        Orcamento orcamento = repository
                .findByIdOrcamentoAndIdTenant(idOrcamento, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orçamento não encontrado."));

        OrcamentoVisualizacaoDTO dto = new OrcamentoVisualizacaoDTO();

        mapearCabecalho(orcamento, dto);
        mapearCliente(orcamento, dto);
        mapearItens(orcamento, dto);
        mapearResumo(orcamento, dto);
        mapearHistorico(idOrcamento, dto);

        return dto;
    }

    private void mapearCabecalho(
            Orcamento orcamento,
            OrcamentoVisualizacaoDTO dto) {

        dto.setIdOrcamento(orcamento.getIdOrcamento());
        dto.setNuOrcamento(orcamento.getNuOrcamento());
        dto.setDtEmissao(orcamento.getDtEmissao());
        dto.setDtValido(orcamento.getDtValido());
        dto.setStatus(orcamento.getTpStatus());
    }

    private void mapearCliente(
            Orcamento orcamento,
            OrcamentoVisualizacaoDTO dto) {

        Cliente cliente = orcamento.getCliente();

        ClienteVisualizacaoDTO clienteDto = new ClienteVisualizacaoDTO();
        clienteDto.setIdCliente(cliente.getIdCliente());
        clienteDto.setNome(cliente.getNmCliente());
        clienteDto.setEmail(cliente.getDsEmail());
        clienteDto.setTelefone(cliente.getNuTelefone());

        dto.setCliente(clienteDto);
    }

    private void mapearItens(
            Orcamento orcamento,
            OrcamentoVisualizacaoDTO dto) {

        List<ItemVisualizacaoDTO> itens = new ArrayList<>();

        for (OrcamentoItem item : orcamento.getOrcamentoItem()) {

            ItemVisualizacaoDTO itemDto = new ItemVisualizacaoDTO();
            itemDto.setIdItem(item.getIdOrcamentoItem());
            itemDto.setDescricao(item.getNmCatalogo());
            itemDto.setQuantidade(item.getQtItem());
            itemDto.setPrecoUnitario(item.getVlPrecoUnitario());
            itemDto.setSubtotal(item.getVlPrecoTotal());

            itemDto.setMateriais(
                    mapearMateriais(item));

            itens.add(itemDto);
        }

        dto.setItens(itens);
    }

    private List<MaterialVisualizacaoDTO> mapearMateriais(
            OrcamentoItem item) {

        List<MaterialVisualizacaoDTO> materiais = new ArrayList<>();

        if (item.getOrcamentoItemCampoValor() == null) {
            return materiais;
        }

        for (OrcamentoItemCampoValor campo : item.getOrcamentoItemCampoValor()) {

            MaterialVisualizacaoDTO material = new MaterialVisualizacaoDTO();
            material.setDescricao(campo.getNmCampoPersonalizado());
            // material.setQuantidade(campo.getQtInformada());
            material.setValor(campo.getVlInformado());
            material.setTipo(campo.getTpValor());

            materiais.add(material);
        }

        return materiais;
    }

    private void mapearResumo(
            Orcamento orcamento,
            OrcamentoVisualizacaoDTO dto) {

        dto.setMetodoPrecificacao(
                orcamento.getEmpresaMetodoPrecificacao()
                        .getNmMetodoPrecificacao() );

        dto.setVlPrecoBase(orcamento.getVlPrecoBase());
        dto.setVlPrecoFinal(orcamento.getVlPrecoFinal());
    }

    private void mapearHistorico(
            Long idOrcamento,
            OrcamentoVisualizacaoDTO dto) {

        List<OrcamentoStatusHistorico> historicos = historicoRepository
                .findByOrcamento_IdOrcamentoOrderByDtCadastroAsc(idOrcamento);

        List<StatusHistoricoVisualizacaoDTO> historicoDto = historicos.stream().map(h -> {

            StatusHistoricoVisualizacaoDTO s = new StatusHistoricoVisualizacaoDTO();

            s.setStatusAnterior(h.getTpStatusAnterior());
            s.setStatusAtual(h.getTpStatusAtual());
            s.setDataHora(h.getDtCadastro());
            s.setUsuario(h.getUsuario().getNome());

            return s;

        }).toList();

        dto.setHistoricoStatus(historicoDto);
    }

}
