package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.CampoPersonalizadoRequest;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CampoPersonalizado;
import com.api_orcafacil.repository.CampoPersonalizadoRepository;

@Service
public class CampoPersonalizadoService {

    private final CampoPersonalizadoRepository repository;
    private final TenantContextService tenantContextService;

    public CampoPersonalizadoService(CampoPersonalizadoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<CampoPersonalizado> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public CampoPersonalizado buscar(Long id) {
        return repository.findByIdCampoPersonalizadoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Campo nao encontrado"));
    }

    @Transactional
    public CampoPersonalizado salvar(CampoPersonalizadoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        CampoPersonalizado campo = request.getIdCampoPersonalizado() != null
                ? buscar(request.getIdCampoPersonalizado())
                : new CampoPersonalizado();
        campo.setIdOrganizacao(idOrganizacao);
        campo.setCdCampoPersonalizado(request.getCdCampoPersonalizado());
        campo.setNmCampoPersonalizado(request.getNmCampoPersonalizado());
        campo.setDsCampoPersonalizado(request.getDsCampoPersonalizado());
        campo.setTpCampoPersonalizado(request.getTpCampoPersonalizado());
        if (request.getTpCampoValor() != null) {
            campo.setTpCampoValor(request.getTpCampoValor());
        }
        validarCodigo(campo);
        return repository.save(campo);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    private void validarCodigo(CampoPersonalizado campo) {
        repository.findByCdCampoPersonalizadoAndIdOrganizacao(campo.getCdCampoPersonalizado(), campo.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCampoPersonalizado().equals(campo.getIdCampoPersonalizado())) {
                        throw new ConflictException("Codigo de campo ja cadastrado");
                    }
                });
    }
}
