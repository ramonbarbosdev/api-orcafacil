package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.dto.condicao.CondicaoPagamentoRequest;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.CondicaoPagamento;
import com.api_orcafacil.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.repository.OrcamentoRepository;

@Service
public class CondicaoPagamentoService {

    private final CondicaoPagamentoRepository repository;
    private final OrcamentoRepository orcamentoRepository;
    private final TenantContextService tenantContextService;

    public CondicaoPagamentoService(CondicaoPagamentoRepository repository,
            OrcamentoRepository orcamentoRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.orcamentoRepository = orcamentoRepository;
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
    public CondicaoPagamento salvar(CondicaoPagamentoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        CondicaoPagamento objeto = request.getIdCondicaoPagamento() != null
                ? buscar(request.getIdCondicaoPagamento())
                : new CondicaoPagamento();
        objeto.setIdOrganizacao(idOrganizacao);
        objeto.setCdCondicaoPagamento(request.getCdCondicaoPagamento());
        objeto.setNmCondicaoPagamento(request.getNmCondicaoPagamento());
        validarCodigo(objeto);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        CondicaoPagamento condicao = buscar(id);
        if (orcamentoRepository.existsByIdCondicaoPagamento(id)) {
            throw new BusinessException("Condicao de pagamento possui orcamentos vinculados e nao pode ser excluida");
        }
        repository.delete(condicao);
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
