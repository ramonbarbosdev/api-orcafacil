package com.api_orcafacil.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.model.ConfiguracaoOrcamento;
import com.api_orcafacil.repository.ConfiguracaoOrcamentoRepository;

@Service
public class ConfiguracaoOrcamentoService {

    private final ConfiguracaoOrcamentoRepository repository;
    private final TenantContextService tenantContextService;

    public ConfiguracaoOrcamentoService(ConfiguracaoOrcamentoRepository repository,
            TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public ConfiguracaoOrcamento obter() {
        return obterOuCriarPadrao();
    }

    @Transactional
    public ConfiguracaoOrcamento salvar(ConfiguracaoOrcamento dados) {
        ConfiguracaoOrcamento atual = obterOuCriarPadrao();
        atual.setPrefixoNumero(dados.getPrefixoNumero());
        atual.setValidadeDias(dados.getValidadeDias());
        atual.setTermosPadrao(dados.getTermosPadrao());
        return repository.save(atual);
    }

    public ConfiguracaoOrcamento obterPrimeiroObjeto() {
        return obterOuCriarPadrao();
    }

    private ConfiguracaoOrcamento obterOuCriarPadrao() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return repository.findFirstByIdOrganizacao(idOrganizacao).orElseGet(() -> {
            ConfiguracaoOrcamento cfg = new ConfiguracaoOrcamento();
            cfg.setIdOrganizacao(idOrganizacao);
            cfg.setPrefixoNumero("ORC");
            cfg.setValidadeDias(30);
            return repository.save(cfg);
        });
    }
}
