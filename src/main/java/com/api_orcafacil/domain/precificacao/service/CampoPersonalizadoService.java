package com.api_orcafacil.domain.precificacao.service;

import java.util.HashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;

@Service
public class CampoPersonalizadoService {

    @Autowired
    private CampoPersonalizadoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<CampoPersonalizado, Long> ID_FUNCTION = CampoPersonalizado::getIdCampoPersonalizado;

    @Transactional(rollbackFor = Exception.class)
    public CampoPersonalizado salvar(CampoPersonalizado objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(CampoPersonalizado objeto) throws Exception {

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(objeto.getCdCampoPersonalizado(), objeto.getIdTenant()),
                ID_FUNCTION,
                "O códgo do máterial já cadastrado para esta empresa");

    }

}
