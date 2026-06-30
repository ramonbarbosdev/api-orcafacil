package com.api_orcafacil.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.OrcamentoRepository;
import com.api_orcafacil.repository.central.CentralOrcamentoPublicoRepository;
import com.api_orcafacil.tenant.OrganizationResolver;
import com.api_orcafacil.tenant.TenantDescriptor;
import com.api_orcafacil.tenant.TenantRuntimeContext;
import com.api_orcafacil.tenant.central.model.CentralOrcamentoPublico;

@Service
@ConditionalOnProperty(name = "app.saas.central.enabled", havingValue = "true")
public class OrcamentoPublicoService {

    public record ReferenciaPublica(String cdPublico, Long idOrganizacao, Long idOrcamento) {
    }

    private final CentralOrcamentoPublicoRepository repository;
    private final OrganizationResolver organizationResolver;
    private final OrcamentoRepository orcamentoRepository;
    private final ObjectProvider<OrcamentoPublicoService> self;

    public OrcamentoPublicoService(
            CentralOrcamentoPublicoRepository repository,
            OrganizationResolver organizationResolver,
            OrcamentoRepository orcamentoRepository,
            ObjectProvider<OrcamentoPublicoService> self) {
        this.repository = repository;
        this.organizationResolver = organizationResolver;
        this.orcamentoRepository = orcamentoRepository;
        this.self = self;
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void registrar(String cdPublico, Long idOrganizacao, Long idOrcamento) {
        if (cdPublico == null || cdPublico.isBlank()) {
            return;
        }
        CentralOrcamentoPublico registro = repository.findById(cdPublico)
                .orElseGet(CentralOrcamentoPublico::new);
        registro.setCdPublico(cdPublico);
        registro.setIdOrganizacao(idOrganizacao);
        registro.setIdOrcamento(idOrcamento);
        repository.save(registro);
    }

    @Transactional(transactionManager = "centralTransactionManager")
    public void excluir(String cdPublico, Long idOrganizacao, Long idOrcamento) {
        if (cdPublico != null && !cdPublico.isBlank()) {
            repository.deleteById(cdPublico);
            return;
        }
        if (idOrganizacao != null && idOrcamento != null) {
            repository.deleteByIdOrganizacaoAndIdOrcamento(idOrganizacao, idOrcamento);
        }
    }

    @Transactional(transactionManager = "centralTransactionManager", readOnly = true)
    public Optional<ReferenciaPublica> buscarReferencia(String cdPublico) {
        return repository.findById(cdPublico)
                .map(registro -> new ReferenciaPublica(
                        registro.getCdPublico(),
                        registro.getIdOrganizacao(),
                        registro.getIdOrcamento()));
    }

    public <T> T executarComCdPublico(String cdPublico, Function<ReferenciaPublica, T> acao) {
        ReferenciaPublica referencia = resolverReferencia(cdPublico);
        return executarComOrganizacao(referencia.idOrganizacao(), () -> acao.apply(referencia));
    }

    public ReferenciaPublica resolverReferencia(String cdPublico) {
        return repository.findById(cdPublico)
                .map(registro -> new ReferenciaPublica(
                        registro.getCdPublico(),
                        registro.getIdOrganizacao(),
                        registro.getIdOrcamento()))
                .orElseGet(() -> registrarAPartirDoTenantAtual(cdPublico));
    }

    public <T> T executarComOrganizacao(Long idOrganizacao, Supplier<T> acao) {
        TenantRuntimeContext.CurrentTenant anterior = TenantRuntimeContext.get();
        if (anterior != null && idOrganizacao.equals(anterior.idOrganizacao())) {
            return acao.get();
        }

        TenantDescriptor descriptor = organizationResolver.resolver(idOrganizacao);
        try {
            TenantRuntimeContext.set(new TenantRuntimeContext.CurrentTenant(
                    anterior != null ? anterior.idUsuario() : null,
                    idOrganizacao,
                    anterior != null ? anterior.role() : null,
                    anterior != null ? anterior.permissoes() : List.of(),
                    descriptor));
            return acao.get();
        } finally {
            if (anterior != null) {
                TenantRuntimeContext.set(anterior);
            } else {
                TenantRuntimeContext.clear();
            }
        }
    }

    private ReferenciaPublica registrarAPartirDoTenantAtual(String cdPublico) {
        if (TenantRuntimeContext.get() == null) {
            throw new ResourceNotFoundException("Orcamento nao encontrado");
        }
        Orcamento orcamento = orcamentoRepository.findByCdPublico(cdPublico)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado"));
        ReferenciaPublica referencia = new ReferenciaPublica(
                cdPublico, orcamento.getIdOrganizacao(), orcamento.getIdOrcamento());
        self.getObject().registrar(referencia.cdPublico(), referencia.idOrganizacao(), referencia.idOrcamento());
        return referencia;
    }
}
