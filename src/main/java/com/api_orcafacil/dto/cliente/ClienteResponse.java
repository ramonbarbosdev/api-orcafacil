package com.api_orcafacil.dto.cliente;

import java.time.LocalDateTime;

import com.api_orcafacil.common.TipoCliente;
import com.api_orcafacil.model.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponse {

    private Long idCliente;
    private Long idOrganizacao;
    private TipoCliente tpCliente;
    private String nuCpfcnpj;
    private String nmCliente;
    private String dsEmail;
    private String nuTelefone;
    private String nuCep;
    private String dsLogradouro;
    private String dsComplemento;
    private String dsBairro;
    private String dsCidade;
    private String dsEstado;
    private boolean flAtivo;
    private String dsObservacoes;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;

    public static ClienteResponse from(Cliente c) {
        ClienteResponse r = new ClienteResponse();
        r.setIdCliente(c.getIdCliente());
        r.setIdOrganizacao(c.getIdOrganizacao());
        r.setTpCliente(c.getTpCliente());
        r.setNuCpfcnpj(c.getNuCpfcnpj());
        r.setNmCliente(c.getNmCliente());
        r.setDsEmail(c.getDsEmail());
        r.setNuTelefone(c.getNuTelefone());
        r.setNuCep(c.getNuCep());
        r.setDsLogradouro(c.getDsLogradouro());
        r.setDsComplemento(c.getDsComplemento());
        r.setDsBairro(c.getDsBairro());
        r.setDsCidade(c.getDsCidade());
        r.setDsEstado(c.getDsEstado());
        r.setFlAtivo(c.isFlAtivo());
        r.setDsObservacoes(c.getDsObservacoes());
        r.setDtCriacao(c.getDtCriacao());
        r.setDtAtualizacao(c.getDtAtualizacao());
        return r;
    }
}
