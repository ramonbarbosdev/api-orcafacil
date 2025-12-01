package com.api_orcafacil.service;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.model.Servico;
import com.api_orcafacil.repository.CategoriaServicoRepository;
import com.api_orcafacil.repository.ServicoRepository;


@Service
public class ServicoService {

    @Autowired
    private ServicoRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<Servico, Long> ID_FUNCTION = Servico::getId_servico;

    public static final Function<Servico, String> SEQUENCIA_FUNCTION = Servico::getCd_servico;

    @Transactional(rollbackFor = Exception.class)
    public Servico salvar(Servico objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(Servico objeto) throws Exception {
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION);

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }
    }

    public Servico buscarPorId(Long id) throws Exception {

        Optional<Servico> objeto = repository.findById(id);

        return objeto.isPresent() ? objeto.get() : null;
    }

  
    public String sequencia() throws Exception {

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial());

        return sq_sequencia;
    }
}
