package com.api_orcafacil.dto.orcamento;

import java.util.ArrayList;
import java.util.List;

import com.api_orcafacil.notificacao.dto.NotificacaoCanal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoEnviarRequest {

    /** Canais desejados. Se vazio, usa WhatsApp (se tiver telefone) e/ou e-mail (se tiver e-mail). */
    private List<NotificacaoCanal> canais = new ArrayList<>();

    /** Mensagem opcional. Se vazia, o OrcaFacil monta o texto automaticamente. */
    private String mensagem;
}
