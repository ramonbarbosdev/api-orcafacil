package com.api_orcafacil.domain.precificacao.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;
import com.api_orcafacil.domain.servico.model.CategoriaServico;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.enums.TipoPrecificacao;
import com.api_orcafacil.util.TenantUtil;

@Service
public class EmpresaMetodoPrecificacaoService {

    @Autowired
    private EmpresaMetodoPrecificacaoRepository repository;

    @Autowired
    private MetodoPrecificacaoRepository metodoPrecificacaoRepository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<EmpresaMetodoPrecificacao, Long> ID_FUNCTION = EmpresaMetodoPrecificacao::getIdEmpresaMetodoPrecificacao;

    @Transactional(rollbackFor = Exception.class)
    public EmpresaMetodoPrecificacao salvar(
            String idTenant,
            EmpresaMetodoPrecificacao objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(EmpresaMetodoPrecificacao objeto) throws Exception {

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(objeto.getIdMetodoPrecificacao(), objeto.getIdTenant()),
                ID_FUNCTION,
                "Método de precificação já cadastrado para esta empresa");

    }

    public EmpresaMetodoPrecificacao buscarPorId(Long id) {

        EmpresaMetodoPrecificacao objeto = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Empresa Método de Precificação não encontrado"));
        return objeto;
    }

    public EmpresaMetodoPrecificacao obterOuCriarPadrao(String idTenant) {

        EmpresaMetodoPrecificacao objeto = repository.findByIdTenant(idTenant)
                .orElseGet(() -> {

                    EmpresaMetodoPrecificacao cfg = criarMetodoPrecificacaoPadrao(idTenant);

                    return repository.save(cfg);
                });

        return objeto;
    }

    @Transactional
    public EmpresaMetodoPrecificacao obterEmpresaMetodoPrecificacaoSimples(
            String idTenant) {

        MetodoPrecificacao metodoSimples = metodoPrecificacaoRepository
                .findByCdMetodoPrecificacao(TipoPrecificacao.SIMPLES)
                .orElseThrow(() -> new IllegalStateException(
                        "Método de precificação SIMPLES não cadastrado"));

        return repository
                .findByIdTenantAndIdMetodoPrecificacao(
                        idTenant,
                        metodoSimples.getIdMetodoPrecificacao())
                .orElseGet(() -> {
                    EmpresaMetodoPrecificacao cfg = criarMetodoPrecificacaoPadrao(idTenant);

                    return repository.save(cfg);
                });
    }

    private EmpresaMetodoPrecificacao criarMetodoPrecificacaoPadrao(String idTenant) {

        MetodoPrecificacao metodo = metodoPrecificacaoRepository
                .findByCdMetodoPrecificacao(TipoPrecificacao.SIMPLES)
                .orElseThrow(() -> new IllegalStateException(
                        "Método de precificação padrão (SIMPLES) não cadastrado"));

        EmpresaMetodoPrecificacao cfg = new EmpresaMetodoPrecificacao();
        cfg.setIdTenant(idTenant);
        cfg.setIdMetodoPrecificacao(metodo.getIdMetodoPrecificacao()); // se tiver relacionamento
        cfg.setConfiguracao(Map.of()); // configuração neutra

        return repository.save(cfg);

    }

}
