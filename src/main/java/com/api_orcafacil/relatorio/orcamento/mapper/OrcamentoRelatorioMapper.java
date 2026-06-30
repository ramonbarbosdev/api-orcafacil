package com.api_orcafacil.relatorio.orcamento.mapper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import com.api_orcafacil.model.OrcamentoItemCampoValor;
import com.api_orcafacil.relatorio.orcamento.dto.OrcamentoItemRelatorioDTO;
import com.api_orcafacil.relatorio.orcamento.dto.OrcamentoRelatorioDTO;

@Component
public class OrcamentoRelatorioMapper {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat MOEDA = new DecimalFormat(
            "#,##0.00", DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR")));

    public OrcamentoRelatorioDTO mapear(Orcamento orcamento, String nomeOrganizacao) {
        OrcamentoRelatorioDTO dto = new OrcamentoRelatorioDTO();
        mapearCabecalho(orcamento, nomeOrganizacao, dto);
        mapearCliente(orcamento, dto);
        mapearItens(orcamento, dto);
        mapearResumo(orcamento, dto);
        mapearComplementos(orcamento, dto);
        return dto;
    }

    private void mapearCabecalho(Orcamento orcamento, String nomeOrganizacao, OrcamentoRelatorioDTO dto) {
        dto.setNuOrcamento(orcamento.getNuOrcamento());
        dto.setDtEmissaoFormatada(formatarData(orcamento.getDtEmissao()));
        dto.setDtValidoFormatada(formatarData(orcamento.getDtValido()));
        dto.setNmEmpresa(nomeOrganizacao);
    }

    private void mapearCliente(Orcamento orcamento, OrcamentoRelatorioDTO dto) {
        Cliente cliente = orcamento.getCliente();
        if (cliente == null) {
            dto.setClienteNome("");
            dto.setClienteCpfCnpj("");
            return;
        }
        dto.setClienteNome(valorOuVazio(cliente.getNmCliente()));
        dto.setClienteCpfCnpj(valorOuVazio(cliente.getNuCpfcnpj()));
    }

    private void mapearItens(Orcamento orcamento, OrcamentoRelatorioDTO dto) {
        List<OrcamentoItemRelatorioDTO> produtos = new ArrayList<>();
        List<OrcamentoItemRelatorioDTO> servicos = new ArrayList<>();
        if (orcamento.getItens() != null) {
            for (OrcamentoItem item : orcamento.getItens()) {
                OrcamentoItemRelatorioDTO itemDto = mapearItem(item);
                if (item.getTpItem() == TipoItem.Servico) {
                    servicos.add(itemDto);
                } else {
                    produtos.add(itemDto);
                }
            }
        }
        dto.setProdutos(produtos);
        dto.setServicos(servicos);
        dto.setPossuiProdutos(!produtos.isEmpty());
        dto.setPossuiServicos(!servicos.isEmpty());
    }

    private OrcamentoItemRelatorioDTO mapearItem(OrcamentoItem item) {
        OrcamentoItemRelatorioDTO itemDto = new OrcamentoItemRelatorioDTO();
        itemDto.setCodigo(item.getCdCatalogo());
        itemDto.setDescricao(item.getNmCatalogo());
        itemDto.setQuantidade(item.getQtItem());
        itemDto.setPrecoCusto(item.getVlCustoUnitario());
        itemDto.setPrecoUnitario(item.getVlPrecoUnitario());
        itemDto.setSubtotal(item.getVlPrecoTotal());
        itemDto.setMateriaisTexto(formatarMateriais(item));
        return itemDto;
    }

    private String formatarMateriais(OrcamentoItem item) {
        if (item.getCamposValor() == null || item.getCamposValor().isEmpty()) {
            return "";
        }
        List<String> linhas = new ArrayList<>();
        for (OrcamentoItemCampoValor campo : item.getCamposValor()) {
            linhas.add(formatarLinhaMaterial(campo));
        }
        return String.join("\n", linhas);
    }

    private String formatarLinhaMaterial(OrcamentoItemCampoValor campo) {
        StringBuilder linha = new StringBuilder("Material: ");
        linha.append(valorOuVazio(campo.getNmCampoPersonalizado()));
        if (campo.getDsDescricao() != null && !campo.getDsDescricao().isBlank()) {
            linha.append(" - ").append(campo.getDsDescricao().trim());
        }
        if (campo.getVlInformado() != null && campo.getVlInformado().compareTo(BigDecimal.ZERO) != 0) {
            linha.append(" | R$ ").append(MOEDA.format(campo.getVlInformado()));
        }
        return linha.toString();
    }

    private void mapearResumo(Orcamento orcamento, OrcamentoRelatorioDTO dto) {
        dto.setVlPrecoBase(orcamento.getVlPrecoBase());
        dto.setVlPrecoFinal(orcamento.getVlPrecoFinal());
        dto.setTotalDesconto(BigDecimal.ZERO);
    }

    private void mapearComplementos(Orcamento orcamento, OrcamentoRelatorioDTO dto) {
        dto.setObservacoes(orcamento.getDsObservacoes());
        String condicao = orcamento.getNmCondicaoPagamento();
        if (condicao != null && !condicao.isBlank()) {
            dto.setCondicaoPagamentoTexto("Condicao de pagamento: " + condicao);
        } else {
            dto.setCondicaoPagamentoTexto("");
        }
        Integer prazo = orcamento.getNuPrazoEntrega();
        if (prazo != null) {
            dto.setPrazoEntregaTexto("Previsao de entrega: " + prazo + " dias");
        } else {
            dto.setPrazoEntregaTexto("");
        }
    }

    private String formatarData(java.time.LocalDate data) {
        return data != null ? data.format(DATA_BR) : "";
    }

    private String valorOuVazio(String valor) {
        return valor != null ? valor : "";
    }
}
