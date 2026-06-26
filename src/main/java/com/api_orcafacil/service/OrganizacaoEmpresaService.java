package com.api_orcafacil.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.organizacao.OrganizacaoEmpresaDTO;
import com.api_orcafacil.dto.organizacao.OrganizacaoEmpresaRequestDTO;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.repository.central.CentralOrganizacaoRepository;
import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrganizacaoEmpresaService {

    private final CentralOrganizacaoRepository organizacaoRepository;
    private final TenantContextService tenantContextService;

    public OrganizacaoEmpresaService(
            CentralOrganizacaoRepository organizacaoRepository,
            TenantContextService tenantContextService) {
        this.organizacaoRepository = organizacaoRepository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public OrganizacaoEmpresaDTO obterAtual() {
        CentralOrganizacao organizacao = buscarOrganizacaoAtual();
        return paraDto(organizacao);
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public OrganizacaoEmpresaDTO salvar(OrganizacaoEmpresaRequestDTO request) {
        CentralOrganizacao organizacao = buscarOrganizacaoAtual();
        organizacao.setDsDocumento(sanitizarDocumento(request.cdEmpresa()));
        organizacao.setNmOrganizacao(request.nmEmpresa().trim());
        organizacao.setDsEmail(normalizarTexto(request.dsEmail()));
        organizacao.setNuTelefone(normalizarTexto(request.nuTelefone()));
        return paraDto(organizacaoRepository.save(organizacao));
    }

    private CentralOrganizacao buscarOrganizacaoAtual() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return organizacaoRepository.findById(idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacao nao encontrada"));
    }

    private OrganizacaoEmpresaDTO paraDto(CentralOrganizacao organizacao) {
        return new OrganizacaoEmpresaDTO(
                organizacao.getIdOrganizacao(),
                organizacao.getIdPlanoAssinatura(),
                organizacao.getDsDocumento(),
                organizacao.getNmOrganizacao(),
                organizacao.getDsEmail(),
                organizacao.getNuTelefone());
    }

    private String sanitizarDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        String apenasDigitos = documento.replaceAll("\\D", "");
        return apenasDigitos.isBlank() ? null : apenasDigitos;
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }
}
