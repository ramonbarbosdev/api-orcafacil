package com.api_orcafacil.tenant.central;

import com.api_orcafacil.dto.precificacao.PlanoAssinaturaResponse;
import com.api_orcafacil.tenant.central.model.CentralPlanoAssinatura;

public final class CentralPlanoAssinaturaMapper {

    private CentralPlanoAssinaturaMapper() {
    }

    public static PlanoAssinaturaResponse toResponse(CentralPlanoAssinatura plano) {
        PlanoAssinaturaResponse response = new PlanoAssinaturaResponse();
        response.setIdPlanoAssinatura(plano.getIdPlanoAssinatura());
        response.setNmPlanoAssinatura(plano.getNmPlanoAssinatura());
        response.setVlMensal(plano.getVlMensal() != null ? plano.getVlMensal().doubleValue() : null);
        response.setNuLimiteMensagens(plano.getNuLimiteMensagens());
        response.setNuLimiteAtendentes(plano.getNuLimiteAtendentes());
        response.setFlAtivo(plano.isFlAtivo());
        response.setDtCriacao(plano.getDtCriacao());
        response.setDtAtualizacao(plano.getDtAtualizacao());
        return response;
    }
}
