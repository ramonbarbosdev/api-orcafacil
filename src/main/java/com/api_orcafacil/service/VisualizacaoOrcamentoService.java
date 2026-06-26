package com.api_orcafacil.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.api_orcafacil.dto.orcamento.ClienteVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.ItemVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.MaterialVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.OrcamentoVisualizacaoDTO;
import com.api_orcafacil.dto.orcamento.StatusHistoricoVisualizacaoDTO;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;
import com.api_orcafacil.model.OrcamentoStatusHistorico;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.service.logo.OrganizacaoLogoService;

@Service
public class VisualizacaoOrcamentoService {

    private final OrcamentoRepository repository;
    private final OrcamentoStatusHistoricoService historicoService;
    private final ObjectProvider<NamedParameterJdbcTemplate> centralJdbc;
    private final OrganizacaoLogoService organizacaoLogoService;

    public VisualizacaoOrcamentoService(OrcamentoRepository repository,
            OrcamentoStatusHistoricoService historicoService,
            ObjectProvider<NamedParameterJdbcTemplate> centralJdbcProvider,
            OrganizacaoLogoService organizacaoLogoService) {
        this.repository = repository;
        this.historicoService = historicoService;
        this.centralJdbc = centralJdbcProvider;
        this.organizacaoLogoService = organizacaoLogoService;
    }

    public OrcamentoVisualizacaoDTO visualizarPublico(Long idOrcamento, Long idOrganizacao) {
        Orcamento orcamento = repository.findByIdOrcamentoAndIdOrganizacao(idOrcamento, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        OrcamentoVisualizacaoDTO dto = new OrcamentoVisualizacaoDTO();
        mapearCabecalho(orcamento, dto);
        mapearCliente(orcamento, dto);
        mapearItens(orcamento, dto);
        mapearResumo(orcamento, dto);
        mapearHistorico(idOrcamento, dto);
        return dto;
    }

    public OrcamentoVisualizacaoDTO visualizarPorCdPublico(String cdPublico) {
        Orcamento orcamento = repository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        return visualizarPublico(orcamento.getIdOrcamento(), orcamento.getIdOrganizacao());
    }

    private void mapearCabecalho(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        dto.setIdOrcamento(orcamento.getIdOrcamento());
        dto.setNuOrcamento(orcamento.getNuOrcamento());
        dto.setDtEmissao(orcamento.getDtEmissao());
        dto.setDtValido(orcamento.getDtValido());
        dto.setStatus(orcamento.getTpStatus());
        dto.setNmEmpresa(buscarNomeOrganizacao(orcamento.getIdOrganizacao()));
        boolean possuiLogo = organizacaoLogoService.possuiLogo(orcamento.getIdOrganizacao());
        dto.setPossuiLogo(possuiLogo);
        if (possuiLogo && orcamento.getCdPublico() != null) {
            dto.setLogoUrl(OrganizacaoLogoService.URL_LOGO_PUBLICA_PREFIXO + orcamento.getCdPublico() + "/logo");
        }
        dto.setCondicaoPagamento(orcamento.getNmCondicaoPagamento());
        dto.setNuPrazoEntrega(orcamento.getNuPrazoEntrega());
        dto.setTotalDesconto(new BigDecimal("0.00"));
        dto.setObservacoes(orcamento.getDsObservacoes());
    }

    private String buscarNomeOrganizacao(Long idOrganizacao) {
        NamedParameterJdbcTemplate jdbc = centralJdbc.getIfAvailable();
        if (jdbc == null) {
            return null;
        }
        try {
            return jdbc.queryForObject(
                    "select nm_organizacao from organizacao where id_organizacao = :id",
                    Map.of("id", idOrganizacao),
                    String.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private void mapearCliente(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        Cliente cliente = orcamento.getCliente();
        if (cliente == null) {
            return;
        }
        ClienteVisualizacaoDTO clienteDto = new ClienteVisualizacaoDTO();
        clienteDto.setIdCliente(cliente.getIdCliente());
        clienteDto.setNome(cliente.getNmCliente());
        clienteDto.setCpfCnpj(cliente.getNuCpfcnpj());
        clienteDto.setEmail(cliente.getDsEmail());
        clienteDto.setTelefone(cliente.getNuTelefone());
        dto.setCliente(clienteDto);
    }

    private void mapearItens(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        List<ItemVisualizacaoDTO> itens = new ArrayList<>();
        for (OrcamentoItem item : orcamento.getItens()) {
            ItemVisualizacaoDTO itemDto = new ItemVisualizacaoDTO();
            itemDto.setIdItem(item.getIdOrcamentoItem());
            itemDto.setCodigo(item.getCdCatalogo());
            itemDto.setDescricao(item.getNmCatalogo());
            itemDto.setQuantidade(item.getQtItem());
            itemDto.setPrecoCusto(item.getVlCustoUnitario());
            itemDto.setPrecoUnitario(item.getVlPrecoUnitario());
            itemDto.setSubtotal(item.getVlPrecoTotal());
            itemDto.setTipo(item.getTpItem());
            itemDto.setMateriais(mapearMateriais(item));
            itens.add(itemDto);
        }
        dto.setItens(itens);
    }

    private List<MaterialVisualizacaoDTO> mapearMateriais(OrcamentoItem item) {
        List<MaterialVisualizacaoDTO> materiais = new ArrayList<>();
        if (item.getCamposValor() == null) {
            return materiais;
        }
        for (OrcamentoItemCampoValor campo : item.getCamposValor()) {
            MaterialVisualizacaoDTO material = new MaterialVisualizacaoDTO();
            material.setNome(campo.getNmCampoPersonalizado());
            material.setDescricao(campo.getDsDescricao());
            material.setValor(campo.getVlInformado());
            material.setTipo(campo.getTpValor());
            materiais.add(material);
        }
        return materiais;
    }

    private void mapearResumo(Orcamento orcamento, OrcamentoVisualizacaoDTO dto) {
        if (orcamento.getEmpresaMetodoPrecificacao() != null) {
            dto.setMetodoPrecificacao(orcamento.getEmpresaMetodoPrecificacao().getNmMetodoPrecificacao());
        }
        dto.setVlPrecoBase(orcamento.getVlPrecoBase());
        dto.setVlPrecoFinal(orcamento.getVlPrecoFinal());
    }

    private void mapearHistorico(Long idOrcamento, OrcamentoVisualizacaoDTO dto) {
        List<StatusHistoricoVisualizacaoDTO> historicoDto = historicoService.listarPorOrcamento(idOrcamento).stream()
                .map(this::mapearHistoricoItem)
                .toList();
        dto.setHistoricoStatus(historicoDto);
    }

    private StatusHistoricoVisualizacaoDTO mapearHistoricoItem(OrcamentoStatusHistorico h) {
        StatusHistoricoVisualizacaoDTO s = new StatusHistoricoVisualizacaoDTO();
        s.setStatusAnterior(h.getTpStatusAnterior());
        s.setStatusAtual(h.getTpStatusNovo());
        s.setDataHora(h.getDtCriacao());
        return s;
    }
}
