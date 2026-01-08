package com.api_orcafacil.domain.catalogo.service;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.catalogo.model.Catalogo;
import com.api_orcafacil.domain.catalogo.repository.CatalogoRepository;

import com.api_orcafacil.domain.sistema.service.ValidacaoService;

@Service
public class CatalogoService {

    @Autowired
    private CatalogoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<Catalogo, Long> ID_FUNCTION = Catalogo::getIdCatalogo;

    public static final Function<Catalogo, String> SEQUENCIA_FUNCTION = Catalogo::getCdCatalogo;

    @Transactional(rollbackFor = Exception.class)
    public Catalogo salvar(Catalogo objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(Catalogo objeto) throws Exception {

         if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto), objeto.getIdTenant()),
                ID_FUNCTION);

       
    }

    public String sequencia() throws Exception {

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial());

        return sq_sequencia;
    }
}
