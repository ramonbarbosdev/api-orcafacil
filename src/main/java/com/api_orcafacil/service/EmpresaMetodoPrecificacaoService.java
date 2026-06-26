package com.api_orcafacil.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoRequest;
import com.api_orcafacil.dto.precificacao.EmpresaMetodoPrecificacaoResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.repository.MetodoPrecificacaoRepository;

@Service
public class EmpresaMetodoPrecificacaoService {

    private final EmpresaMetodoPrecificacaoRepository repository;
    private final MetodoPrecificacaoRepository metodoPrecificacaoRepository;
    private final TenantContextService tenantContextService;

    public EmpresaMetodoPrecificacaoService(EmpresaMetodoPrecificacaoRepository repository,
            MetodoPrecificacaoRepository metodoPrecificacaoRepository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.metodoPrecificacaoRepository = metodoPrecificacaoRepository;
        this.tenantContextService = tenantContextService;
    }

    @Transactional(readOnly = true)
    public List<EmpresaMetodoPrecificacaoResponse> listar() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return repository.findByIdOrganizacaoWithMetodo(idOrganizacao).stream()
                .map(this::montarResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaMetodoPrecificacaoResponse buscarPorId(Long id) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        EmpresaMetodoPrecificacao objeto = repository.findByIdAndOrganizacaoWithMetodo(id, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa metodo nao encontrado"));
        return montarResponse(objeto);
    }

    @Transactional(readOnly = true)
    public EmpresaMetodoPrecificacao buscarEntidadePorId(Long id) {
        return repository.findByIdEmpresaMetodoPrecificacaoAndIdOrganizacao(
                        id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa metodo nao encontrado"));
    }

    @Transactional
    public EmpresaMetodoPrecificacaoResponse salvar(EmpresaMetodoPrecificacaoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        EmpresaMetodoPrecificacao objeto = request.getIdEmpresaMetodoPrecificacao() != null
                ? buscarEntidadePorId(request.getIdEmpresaMetodoPrecificacao())
                : new EmpresaMetodoPrecificacao();
        objeto.setIdOrganizacao(idOrganizacao);
        objeto.setIdMetodoPrecificacao(request.getIdMetodoPrecificacao());
        objeto.setConfiguracao(request.getConfiguracao());
        validarDuplicidade(objeto);
        EmpresaMetodoPrecificacao salvo = repository.save(objeto);
        MetodoPrecificacao metodo = metodoPrecificacaoRepository.findById(salvo.getIdMetodoPrecificacao())
                .orElseThrow(() -> new ResourceNotFoundException("Metodo de precificacao nao encontrado"));
        return EmpresaMetodoPrecificacaoResponse.from(salvo, metodo);
    }

    public EmpresaMetodoPrecificacao obterEmpresaMetodoPrecificacaoSimples() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        MetodoPrecificacao metodoSimples = metodoPrecificacaoRepository.findByCdMetodoPrecificacao(TipoPrecificacao.SIMPLES)
                .orElseThrow(() -> new ResourceNotFoundException("Metodo SIMPLES nao cadastrado"));
        return repository.findByIdOrganizacaoAndIdMetodoPrecificacao(idOrganizacao, metodoSimples.getIdMetodoPrecificacao())
                .orElseGet(() -> repository.save(criarPadrao(idOrganizacao, metodoSimples.getIdMetodoPrecificacao())));
    }

    private EmpresaMetodoPrecificacao criarPadrao(Long idOrganizacao, Long idMetodo) {
        EmpresaMetodoPrecificacao cfg = new EmpresaMetodoPrecificacao();
        cfg.setIdOrganizacao(idOrganizacao);
        cfg.setIdMetodoPrecificacao(idMetodo);
        cfg.setConfiguracao(Map.of());
        return cfg;
    }

    private void validarDuplicidade(EmpresaMetodoPrecificacao objeto) {
        repository.findByMetodoAndOrganizacao(objeto.getIdMetodoPrecificacao(), objeto.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdEmpresaMetodoPrecificacao().equals(objeto.getIdEmpresaMetodoPrecificacao())) {
                        throw new ConflictException("Metodo de precificacao ja cadastrado para esta organizacao");
                    }
                });
    }

    private EmpresaMetodoPrecificacaoResponse montarResponse(EmpresaMetodoPrecificacao empresa) {
        return EmpresaMetodoPrecificacaoResponse.from(empresa, empresa.getMetodoPrecificacao());
    }
}
