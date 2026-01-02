package com.api_orcafacil.domain.cliente.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.context.TenantContext;
import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.cliente.repository.ClienteRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.util.TenantUtil;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<Cliente, Long> ID_FUNCTION = Cliente::getId_cliente;

    public static final Function<Cliente, String> SEQUENCIA_FUNCTION = Cliente::getNu_cpfcnpj;

    @Transactional(rollbackFor = Exception.class)
    public Cliente salvar(Cliente objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(Cliente objeto) throws Exception {
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION);

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }

    }

    public Cliente buscarPorId(Long id) throws Exception {

        Optional<Cliente> objeto = repository.findById(id);

        return objeto.isPresent() ? objeto.get() : null;
    }

}
