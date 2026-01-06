package com.api_orcafacil.domain.precificacao.service;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;
import com.api_orcafacil.domain.empresa.repository.PlanoAssinaturaRepository;
import com.api_orcafacil.domain.orcamento.model.ConfiguracaoOrcamento;
import com.api_orcafacil.domain.precificacao.model.CampoPersonalizado;
import com.api_orcafacil.domain.precificacao.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.model.MetodoAjuste;
import com.api_orcafacil.domain.precificacao.model.MetodoPrecificacao;
import com.api_orcafacil.domain.precificacao.repository.CampoPersonalizadoRepository;
import com.api_orcafacil.domain.precificacao.repository.EmpresaMetodoPrecificacaoRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoAjusteRepository;
import com.api_orcafacil.domain.precificacao.repository.MetodoPrecificacaoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.TenantUtil;

@Service
public class MetodoAjusteService {

    @Autowired
    private MetodoAjusteRepository repository;

    @Autowired
    private EmpresaMetodoPrecificacaoRepository empresaMetodoPrecificacaoRepository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<MetodoAjuste, Long> ID_FUNCTION = MetodoAjuste::getIdMetodoAjuste;

    public static final Function<MetodoAjuste, Long> SEQUENCIA_FUNCTION = MetodoAjuste::getIdCampoPersonalizado;

    @Transactional(rollbackFor = Exception.class)
    public MetodoAjuste salvar(MetodoAjuste objeto) throws Exception {

        validarObjeto(objeto);
        
        return repository.save(objeto);
    }

    public void validarObjeto(MetodoAjuste objeto) throws Exception {
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCampoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION,
                "Não foi possivel salvar. Já existe um ajuste com o campo utilizado."
            );

        Optional<EmpresaMetodoPrecificacao> empresaMetodo = Optional.ofNullable(empresaMetodoPrecificacaoRepository
                .findByIdTenant(objeto.getIdTenant())
                .orElseThrow(() -> new IllegalStateException(
                        "A Empresa Método de precificação não encontrada")));

        objeto.setIdEmpresaMetodoPrecificacao(empresaMetodo.get().getIdEmpresaMetodoPrecificacao());

    }

}
