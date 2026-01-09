package com.api_orcafacil.domain.cliente.service;

import java.time.LocalDate;
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
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.enums.TipoCliente;
import com.api_orcafacil.util.TenantUtil;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<Cliente, Long> ID_FUNCTION = Cliente::getIdCliente;

    public static final Function<Cliente, String> SEQUENCIA_FUNCTION = Cliente::getNuCpfcnpj;

    @Transactional(rollbackFor = Exception.class)
    public Cliente salvar(Cliente objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(Cliente objeto) throws Exception {

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            String tenant = TenantContext.getTenantId();
            objeto.setIdTenant(tenant);
        }

        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto), objeto.getIdTenant()),
                ID_FUNCTION);

    }

    public Cliente buscarPorId(Long id) throws Exception {

        Optional<Cliente> objeto = repository.findById(id);

        return objeto.isPresent() ? objeto.get() : null;
    }

    public Cliente registrarClienteAPartirDoOrcamento(Orcamento obj) throws Exception {

        Cliente entrada = obj.getCliente();

        if (entrada == null) {
            throw new Exception("Objeto cliente está null.");
        }

        if (entrada.getNuCpfcnpj() == null || entrada.getNuCpfcnpj().isEmpty()) {
            throw new Exception("O CPF/CNPJ é obrigatório para continuar!");
        }

        if (entrada.getIdTenant() == null || entrada.getIdTenant().isEmpty()) {
            entrada.setIdTenant(TenantContext.getTenantId());
        }

        Optional<Cliente> clienteExistente = repository.verificarCodigoExistente(
                entrada.getNuCpfcnpj(),
                entrada.getIdTenant());

        Cliente cliente;

        if (clienteExistente.isPresent()) {
            cliente = clienteExistente.get();
        } else {
            cliente = new Cliente();
        }

        cliente.setTpCliente(
                entrada.getNuCpfcnpj().length() > 11
                        ? TipoCliente.Juridico
                        : TipoCliente.Fisico);
        cliente.setNuCpfcnpj(entrada.getNuCpfcnpj());
        cliente.setNmCliente(entrada.getNmCliente());
        cliente.setNuTelefone(entrada.getNuTelefone());
        cliente.setDsEmail(entrada.getDsEmail());
        cliente.setDsObservacoes(entrada.getDsObservacoes());
        cliente.setIdTenant(entrada.getIdTenant());

        cliente = salvar(cliente);

        obj.setCliente(cliente);
        obj.setIdCliente(cliente.getIdCliente());

        return cliente;
    }

}
