package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CondicaoPagamento;
import com.api_orcafacil.repository.CondicaoPagamentoRepository;

@Service
public class CondicaoPagamentoService {

    private final CondicaoPagamentoRepository repository;
    private final TenantContextService tenantContextService;

    public CondicaoPagamentoService(CondicaoPagamentoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<CondicaoPagamento> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public CondicaoPagamento buscar(Long id) {
        return repository.findByIdCondicaoPagamentoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Condicao nao encontrada"));
    }

    @Transactional
    public CondicaoPagamento salvar(CondicaoPagamento objeto) {
        objeto.setIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
        validarCodigo(objeto);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void validarCodigo(CondicaoPagamento objeto) {
        repository.findByCdCondicaoPagamentoAndIdOrganizacao(objeto.getCdCondicaoPagamento(), objeto.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdCondicaoPagamento().equals(objeto.getIdCondicaoPagamento())) {
                        throw new ConflictException("Codigo de condicao ja cadastrado");
                    }
                });
    }
}
