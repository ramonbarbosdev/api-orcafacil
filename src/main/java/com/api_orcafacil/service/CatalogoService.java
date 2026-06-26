package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.dto.catalogo.CatalogoRequest;
import com.api_orcafacil.dto.catalogo.CatalogoResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Catalogo;
import com.api_orcafacil.model.CatalogoCampo;
import com.api_orcafacil.repository.CatalogoCampoRepository;
import com.api_orcafacil.repository.CatalogoRepository;

@Service
public class CatalogoService {

    private final CatalogoRepository repository;
    private final CatalogoCampoRepository campoRepository;
    private final TenantContextService tenantContextService;

    public CatalogoService(CatalogoRepository repository, CatalogoCampoRepository campoRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.campoRepository = campoRepository;
        this.tenantContextService = tenantContextService;
    }

    public List<CatalogoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(CatalogoResponse::from).toList();
    }

    public CatalogoResponse buscar(Long id) {
        Catalogo catalogo = repository.findByIdCatalogoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"));
        return CatalogoResponse.from(catalogo);
    }

    @Transactional
    public CatalogoResponse salvar(CatalogoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Catalogo catalogo = request.getIdCatalogo() != null
                ? repository.findByIdCatalogoAndIdOrganizacao(request.getIdCatalogo(), idOrganizacao)
                        .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"))
                : new Catalogo();

        catalogo.setIdOrganizacao(idOrganizacao);
        catalogo.setTpItem(request.getTpItem() != null ? request.getTpItem() : TipoItem.Produto);
        catalogo.setCdCatalogo(request.getCdCatalogo());
        catalogo.setNmCatalogo(request.getNmCatalogo());
        catalogo.setDsCatalogo(request.getDsCatalogo());
        catalogo.setVlCustoBase(request.getVlCustoBase());
        catalogo.setVlPrecoBase(request.getVlPrecoBase());

        validarCodigo(catalogo);
        catalogo = repository.save(catalogo);
        salvarCampos(catalogo, request.getCampos());
        return CatalogoResponse.from(repository.findById(catalogo.getIdCatalogo()).orElseThrow());
    }

    @Transactional
    public void excluir(Long id) {
        Catalogo catalogo = repository.findByIdCatalogoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"));
        campoRepository.deleteByIdCatalogo(catalogo.getIdCatalogo());
        repository.delete(catalogo);
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void salvarCampos(Catalogo catalogo, List<CatalogoCampo> campos) {
        campoRepository.deleteByIdCatalogo(catalogo.getIdCatalogo());
        if (campos == null) {
            return;
        }
        for (CatalogoCampo campo : campos) {
            campo.setCatalogo(catalogo);
            if (campo.getIdCatalogoCampo() != null && campo.getIdCatalogoCampo() == 0) {
                campo.setIdCatalogoCampo(null);
            }
            campoRepository.save(campo);
        }
        catalogo.setCampos(campos);
    }

    private void validarCodigo(Catalogo catalogo) {
        repository.findByCdCatalogoAndIdOrganizacao(catalogo.getCdCatalogo(), catalogo.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCatalogo().equals(catalogo.getIdCatalogo())) {
                        throw new ConflictException("Codigo de catalogo ja cadastrado");
                    }
                });
    }
}
