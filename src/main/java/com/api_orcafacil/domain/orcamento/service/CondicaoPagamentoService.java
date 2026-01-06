package com.api_orcafacil.domain.orcamento.service;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.orcamento.repository.CondicaoPagamentoRepository;
import com.api_orcafacil.domain.orcamento.repository.ConfiguracaoOrcamentoRepository;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.servico.repository.ServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;

@Service
public class CondicaoPagamentoService {

    @Autowired
    private CondicaoPagamentoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<CodicaoPagamento, Long> ID_FUNCTION = CodicaoPagamento::getIdCondicaoPagamento;

    public static final Function<CodicaoPagamento, String> SEQUENCIA_FUNCTION = CodicaoPagamento::getCdCondicaoPagamento;

    @Transactional(rollbackFor = Exception.class)
    public CodicaoPagamento salvar(CodicaoPagamento objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(CodicaoPagamento objeto) throws Exception {
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION);

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
    }

    public String sequencia() throws Exception {

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial());

        return sq_sequencia;
    }
}
