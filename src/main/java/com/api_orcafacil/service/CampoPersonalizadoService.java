package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.CampoPersonalizadoRequest;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CampoPersonalizado;
import com.api_orcafacil.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.repository.MetodoAjusteRepository;
import com.api_orcafacil.repository.OrcamentoItemCampoValorRepository;

@Service
public class CampoPersonalizadoService {

    private final CampoPersonalizadoRepository repository;
    private final MetodoAjusteRepository metodoAjusteRepository;
    private final OrcamentoItemCampoValorRepository orcamentoItemCampoValorRepository;
    private final TenantContextService tenantContextService;

    public CampoPersonalizadoService(CampoPersonalizadoRepository repository,
            MetodoAjusteRepository metodoAjusteRepository,
            OrcamentoItemCampoValorRepository orcamentoItemCampoValorRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.metodoAjusteRepository = metodoAjusteRepository;
        this.orcamentoItemCampoValorRepository = orcamentoItemCampoValorRepository;
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
        CampoPersonalizado campo = buscar(id);
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        if (metodoAjusteRepository.findByIdCampoPersonalizadoAndIdOrganizacao(id, idOrganizacao).isPresent()) {
            throw new BusinessException("Campo possui metodos de ajuste vinculados e nao pode ser excluido");
        }
        if (orcamentoItemCampoValorRepository.existsByIdCampoPersonalizado(id)) {
            throw new BusinessException("Campo possui itens em orcamentos e nao pode ser excluido");
        }
        repository.delete(campo);
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
