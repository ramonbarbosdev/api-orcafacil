package com.api_orcafacil.dto.orcamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.model.OrcamentoItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoResponse {

    private Long idOrcamento;
    private Long idOrganizacao;
    private String nuOrcamento;
    private LocalDate dtEmissao;
    private LocalDate dtValido;
    private Long idCliente;
    private String nmCliente;
    private Long idEmpresaMetodoPrecificacao;
    private Long idCondicaoPagamento;
    private String nmCondicaoPagamento;
    private Integer nuPrazoEntrega;
    private String dsObservacoes;
    private BigDecimal vlPrecoBase;
    private BigDecimal vlPrecoFinal;
    private StatusOrcamento tpStatus;
    private String cdPublico;
    private List<OrcamentoItem> itens;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static OrcamentoResponse from(Orcamento o) {
        OrcamentoResponse r = new OrcamentoResponse();
        r.setIdOrcamento(o.getIdOrcamento());
        r.setIdOrganizacao(o.getIdOrganizacao());
        r.setNuOrcamento(o.getNuOrcamento());
        r.setDtEmissao(o.getDtEmissao());
        r.setDtValido(o.getDtValido());
        r.setIdCliente(o.getIdCliente());
        r.setNmCliente(o.getNmCliente());
        r.setIdEmpresaMetodoPrecificacao(o.getIdEmpresaMetodoPrecificacao());
        r.setIdCondicaoPagamento(o.getIdCondicaoPagamento());
        r.setNmCondicaoPagamento(o.getNmCondicaoPagamento());
        r.setNuPrazoEntrega(o.getNuPrazoEntrega());
        r.setDsObservacoes(o.getDsObservacoes());
        r.setVlPrecoBase(o.getVlPrecoBase());
        r.setVlPrecoFinal(o.getVlPrecoFinal());
        r.setTpStatus(o.getTpStatus());
        r.setCdPublico(o.getCdPublico());
        r.setItens(o.getItens());
        r.setDtCriacao(o.getDtCriacao());
        r.setDtAtualizacao(o.getDtAtualizacao());
        return r;
    }
}
