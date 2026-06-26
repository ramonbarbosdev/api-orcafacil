package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.repository.CategoriaServicoRepository;

@Service
public class CategoriaServicoService {

    private final CategoriaServicoRepository repository;
    private final TenantContextService tenantContextService;

    public CategoriaServicoService(CategoriaServicoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<CategoriaServico> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public CategoriaServico buscar(Long id) {
        return repository.findByIdCategoriaServicoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada"));
    }

    @Transactional
    public CategoriaServico salvar(CategoriaServico objeto) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        objeto.setIdOrganizacao(idOrganizacao);
        validarCodigo(objeto);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaServico categoria = buscar(id);
        repository.delete(categoria);
    }

    public String sequencia() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(idOrganizacao));
    }

    private void validarCodigo(CategoriaServico objeto) {
        repository.findByCdCategoriaServicoAndIdOrganizacao(objeto.getCdCategoriaServico(), objeto.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCategoriaServico().equals(objeto.getIdCategoriaServico())) {
                        throw new ConflictException("Codigo de categoria ja cadastrado");
                    }
                });
    }
}
