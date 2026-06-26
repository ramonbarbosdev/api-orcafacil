package com.api_orcafacil.dto.servico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.api_orcafacil.model.Servico;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoResponse {

    private Long idServico;
    private Long idOrganizacao;
    private Long idCategoriaServico;
    private String cdServico;
    private String nmServico;
    private String dsServico;
    private BigDecimal vlCusto;
    private BigDecimal vlPreco;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static ServicoResponse from(Servico s) {
        ServicoResponse r = new ServicoResponse();
        r.setIdServico(s.getIdServico());
        r.setIdOrganizacao(s.getIdOrganizacao());
        r.setIdCategoriaServico(s.getIdCategoriaServico());
        r.setCdServico(s.getCdServico());
        r.setNmServico(s.getNmServico());
        r.setDsServico(s.getDsServico());
        r.setVlCusto(s.getVlCusto());
        r.setVlPreco(s.getVlPreco());
        r.setDtCriacao(s.getDtCriacao());
        r.setDtAtualizacao(s.getDtAtualizacao());
        return r;
    }
}
