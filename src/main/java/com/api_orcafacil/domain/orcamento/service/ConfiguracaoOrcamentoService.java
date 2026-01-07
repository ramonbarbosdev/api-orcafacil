package com.api_orcafacil.domain.orcamento.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;

@Service
public class ConfiguracaoOrcamentoService {

    @Autowired
    private ConfiguracaoOrcamentoRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public ConfiguracaoOrcamento obterOuCriarPadrao(String idTenant) {

        return repository.findByIdTenant(idTenant)
                .orElseGet(() -> {
                    Empresa empresa = empresaRepository.findByIdTenant(idTenant)
                            .orElseThrow(() -> new IllegalStateException(
                                    "Empresa não cadastrado"));

                    ConfiguracaoOrcamento cfg = new ConfiguracaoOrcamento();
                    cfg.setIdEmpresa(empresa.getIdEmpresa());
                    cfg.setIdTenant(idTenant);
                    cfg.setPrefixoNumero("ORC");
                    cfg.setValidadeDias(30);

                    return repository.save(cfg);
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public ConfiguracaoOrcamento salvar(
            String idTenant,
            ConfiguracaoOrcamento dados) {

        ConfiguracaoOrcamento atual = obterOuCriarPadrao(idTenant);

        atual.setPrefixoNumero(dados.getPrefixoNumero());
        atual.setValidadeDias(dados.getValidadeDias());
        atual.setTermosPadrao(dados.getTermosPadrao());

        return repository.save(atual);
    }

    public ConfiguracaoOrcamento obterPrimeiroObjeto(String idTenant) {
        Optional<ConfiguracaoOrcamento> objeto = Optional.ofNullable(
                repository.findFirstByIdTenantOrderByIdConfiguracaoOrcamentoAsc(idTenant)
                        .orElseThrow(() -> new IllegalStateException(
                                "Empresa não cadastrado")));

        return objeto.get();
    }
}
