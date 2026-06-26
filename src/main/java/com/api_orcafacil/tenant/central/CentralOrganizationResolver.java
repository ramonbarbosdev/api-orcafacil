package com.api_orcafacil.tenant.central;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.tenant.OrganizationResolver;
import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.TenantDescriptor;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Component
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class CentralOrganizationResolver implements OrganizationResolver {

    private final CentralOrganizacaoRepository organizacaoRepository;

    public CentralOrganizationResolver(CentralOrganizacaoRepository organizacaoRepository) {
        this.organizacaoRepository = organizacaoRepository;
    }

    @Override
    public TenantDescriptor resolver(Long idOrganizacao) {
        CentralOrganizacao organizacao = organizacaoRepository.findById(idOrganizacao)
                .filter(org -> org.isFlAtivo() && org.getStatus() == OrganizationStatus.ATIVA)
                .orElseThrow(() -> new IllegalStateException(
                        "Organizacao ativa nao encontrada no banco central: " + idOrganizacao));

        return new TenantDescriptor(
                organizacao.getIdOrganizacao(),
                organizacao.getSlug(),
                organizacao.getStorageMode(),
                organizacao.getDatabaseName(),
                null,
                null,
                organizacao.getStatus());
    }
}
