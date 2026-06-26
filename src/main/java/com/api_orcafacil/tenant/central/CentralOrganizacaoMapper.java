package com.api_orcafacil.tenant.central;

import com.api_orcafacil.dto.OrganizacaoResponseDTO;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

public final class CentralOrganizacaoMapper {

    private CentralOrganizacaoMapper() {
    }

    public static OrganizacaoResponseDTO toResponse(CentralOrganizacao organizacao) {
        return new OrganizacaoResponseDTO(
                organizacao.getIdOrganizacao(),
                organizacao.getIdPlanoAssinatura(),
                organizacao.getNmOrganizacao(),
                organizacao.getDsDocumento(),
                organizacao.getSlug(),
                organizacao.getDatabaseName(),
                organizacao.getStatus().name(),
                organizacao.isFlAtivo(),
                organizacao.getDtCriacao());
    }
}
