package com.api_orcafacil.dto.organizacao;

public record OrganizacaoEmpresaDTO(
        Long idOrganizacao,
        Long idPlanoAssinatura,
        String cdEmpresa,
        String nmEmpresa,
        String dsEmail,
        String nuTelefone) {
}
