package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.dto.precificacao.MetodoAjusteRequest;
import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.MetodoAjuste;
import com.api_orcafacil.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.repository.MetodoAjusteRepository;

@Service
public class MetodoAjusteService {

    private final MetodoAjusteRepository repository;
    private final CampoPersonalizadoRepository campoPersonalizadoRepository;
    private final EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService;
    private final TenantContextService tenantContextService;

    public MetodoAjusteService(MetodoAjusteRepository repository,
            CampoPersonalizadoRepository campoPersonalizadoRepository,
            EmpresaMetodoPrecificacaoService empresaMetodoPrecificacaoService,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.campoPersonalizadoRepository = campoPersonalizadoRepository;
        this.empresaMetodoPrecificacaoService = empresaMetodoPrecificacaoService;
        this.tenantContextService = tenantContextService;
    }

    public List<MetodoAjuste> listar() {
        return repository.findByIdOrganizacao(tenantContextService.idOrganizacaoObrigatoria());
    }

    public MetodoAjuste buscar(Long id) {
        return repository.findByIdMetodoAjusteAndIdOrganizacao(id, tenantContextService.idOrganizacaoObrigatoria())
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste nao encontrado"));
    }

    @Transactional
    public MetodoAjuste salvar(MetodoAjusteRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        MetodoAjuste objeto = request.getIdMetodoAjuste() != null ? buscar(request.getIdMetodoAjuste()) : new MetodoAjuste();
        Long idEmpresaMetodo = request.getIdEmpresaMetodoPrecificacao() != null
                ? request.getIdEmpresaMetodoPrecificacao()
                : empresaMetodoPrecificacaoService.obterEmpresaMetodoPrecificacaoSimples().getIdEmpresaMetodoPrecificacao();
        empresaMetodoPrecificacaoService.buscarEntidadePorId(idEmpresaMetodo);
        campoPersonalizadoRepository.findByIdCampoPersonalizadoAndIdOrganizacao(
                        request.getIdCampoPersonalizado(), idOrganizacao)
                .orElseThrow(() -> new BusinessException("Campo personalizado nao encontrado"));
        objeto.setIdOrganizacao(idOrganizacao);
        objeto.setIdEmpresaMetodoPrecificacao(idEmpresaMetodo);
        objeto.setIdCampoPersonalizado(request.getIdCampoPersonalizado());
        objeto.setTpAjuste(request.getTpAjuste());
        objeto.setTpOperacao(request.getTpOperacao());
        objeto.setVlCondicao(request.getVlCondicao());
        objeto.setVlIncremento(request.getVlIncremento());
        validarCampo(objeto, idOrganizacao);
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    private void validarCampo(MetodoAjuste objeto, Long idOrganizacao) {
        repository.findByIdCampoPersonalizadoAndIdOrganizacao(objeto.getIdCampoPersonalizado(), idOrganizacao)
                .ifPresent(existente -> {
                    if (objeto.getIdMetodoAjuste() == null
                            || !existente.getIdMetodoAjuste().equals(objeto.getIdMetodoAjuste())) {
                        throw new ConflictException("Ja existe ajuste para este campo");
                    }
                });
    }
}
