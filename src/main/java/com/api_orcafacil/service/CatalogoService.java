package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.TipoItem;
import com.api_orcafacil.dto.catalogo.CatalogoRequest;
import com.api_orcafacil.dto.catalogo.CatalogoResponse;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Catalogo;
import com.api_orcafacil.model.CatalogoCampo;
import com.api_orcafacil.repository.CatalogoRepository;
import com.api_orcafacil.repository.OrcamentoItemRepository;

@Service
public class CatalogoService {

    private final CatalogoRepository repository;
    private final OrcamentoItemRepository orcamentoItemRepository;
    private final TenantContextService tenantContextService;

    public CatalogoService(CatalogoRepository repository,
            OrcamentoItemRepository orcamentoItemRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.orcamentoItemRepository = orcamentoItemRepository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(readOnly = true)
    public List<CatalogoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(CatalogoResponse::from).toList();
    }

    @Transactional(readOnly = true)
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
        salvarCampos(catalogo, request.getCampos());
        return CatalogoResponse.from(repository.save(catalogo));
    }

    @Transactional
    public void excluir(Long id) {
        Catalogo catalogo = repository.findByIdCatalogoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Catalogo nao encontrado"));
        if (orcamentoItemRepository.existsByIdCatalogo(id)) {
            throw new BusinessException("Catalogo possui itens em orcamentos e nao pode ser excluido");
        }
        repository.delete(catalogo);
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void salvarCampos(Catalogo catalogo, List<CatalogoCampo> campos) {
        catalogo.getCampos().clear();
        if (campos == null || campos.isEmpty()) {
            return;
        }
        for (CatalogoCampo campo : campos) {
            CatalogoCampo entidade = new CatalogoCampo();
            entidade.setIdCampoPersonalizado(campo.getIdCampoPersonalizado());
            entidade.setVlPadrao(campo.getVlPadrao());
            entidade.setDsDescricao(campo.getDsDescricao());
            entidade.setFlEditavel(campo.getFlEditavel() != null ? campo.getFlEditavel() : Boolean.TRUE);
            entidade.setOrdem(campo.getOrdem());
            entidade.setCatalogo(catalogo);
            catalogo.getCampos().add(entidade);
        }
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
