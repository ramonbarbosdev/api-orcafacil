package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.dto.servico.ServicoRequest;
import com.api_orcafacil.dto.servico.ServicoResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Servico;
import com.api_orcafacil.repository.ServicoRepository;

@Service
public class ServicoService {

    private final ServicoRepository repository;
    private final TenantContextService tenantContextService;

    public ServicoService(ServicoRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<ServicoResponse> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria())
                .stream().map(ServicoResponse::from).toList();
    }

    public ServicoResponse buscar(Long id) {
        Servico servico = repository.findByIdServicoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado"));
        return ServicoResponse.from(servico);
    }

    @Transactional
    public ServicoResponse salvar(ServicoRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Servico servico = request.getIdServico() != null
                ? repository.findByIdServicoAndIdOrganizacao(request.getIdServico(), idOrganizacao)
                        .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado"))
                : new Servico();
        servico.setIdOrganizacao(idOrganizacao);
        servico.setIdCategoriaServico(request.getIdCategoriaServico());
        servico.setCdServico(request.getCdServico());
        servico.setNmServico(request.getNmServico());
        servico.setDsServico(request.getDsServico());
        servico.setVlCusto(request.getVlCusto());
        servico.setVlPreco(request.getVlPreco());
        validarCodigo(servico);
        return ServicoResponse.from(repository.save(servico));
    }

    @Transactional
    public void excluir(Long id) {
        Servico servico = repository.findByIdServicoAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado"));
        repository.delete(servico);
    }

    public String sequencia() {
        return SequenciaUtil.gerarSequencia(repository.obterSequencial(tenantContextService.idOrganizacaoObrigatoria()));
    }

    private void validarCodigo(Servico servico) {
        repository.findByCdServicoAndIdOrganizacao(servico.getCdServico(), servico.getIdOrganizacao())
                .ifPresent(existente -> {
                    if (!existente.getIdServico().equals(servico.getIdServico())) {
                        throw new ConflictException("Codigo de servico ja cadastrado");
                    }
                });
    }
}
