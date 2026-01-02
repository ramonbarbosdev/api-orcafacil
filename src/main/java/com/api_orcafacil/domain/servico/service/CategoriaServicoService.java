package com.api_orcafacil.domain.servico.service;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.servico.model.CategoriaServico;
import com.api_orcafacil.domain.servico.repository.CategoriaServicoRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;


@Service
public class CategoriaServicoService {

    @Autowired
    private CategoriaServicoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<CategoriaServico, Long> ID_FUNCTION = CategoriaServico::getId_categoriaservico;

    public static final Function<CategoriaServico, String> SEQUENCIA_FUNCTION = CategoriaServico::getCd_categoriaservico;

    @Transactional(rollbackFor = Exception.class)
    public CategoriaServico salvar(CategoriaServico objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(CategoriaServico objeto) throws Exception {
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION);

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
    }

    public CategoriaServico buscarPorId(Long id) throws Exception {

        Optional<CategoriaServico> objeto = repository.findById(id);

        return objeto.isPresent() ? objeto.get() : null;
    }

  
    public String sequencia() throws Exception {

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial());

        return sq_sequencia;
    }
}
