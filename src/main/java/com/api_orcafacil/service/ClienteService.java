package com.api_orcafacil.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.SequenciaUtil;
import com.api_orcafacil.common.TipoCliente;
import com.api_orcafacil.dto.cliente.ClienteRequest;
import com.api_orcafacil.dto.cliente.ClienteResponse;
import com.api_orcafacil.exception.ConflictException;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.Cliente;
import com.api_orcafacil.model.Orcamento;
import com.api_orcafacil.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final TenantContextService tenantContextService;

    public ClienteService(ClienteRepository repository, TenantContextService tenantContextService) {
        this.repository = repository;
        this.tenantContextService = tenantContextService;
    }

    public List<ClienteResponse> listar() {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        return repository.findByIdOrganizacao(idOrganizacao).stream().map(ClienteResponse::from).toList();
    }

    public ClienteResponse buscar(Long id) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Cliente cliente = repository.findByIdClienteAndIdOrganizacao(id, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));
        return ClienteResponse.from(cliente);
    }

    @Transactional
    public ClienteResponse salvar(ClienteRequest request) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        validarDuplicidade(request.getNuCpfcnpj(), idOrganizacao, request.getIdCliente());

        Cliente cliente = request.getIdCliente() != null
                ? repository.findByIdClienteAndIdOrganizacao(request.getIdCliente(), idOrganizacao)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"))
                : new Cliente();

        aplicar(cliente, request, idOrganizacao);
        return ClienteResponse.from(repository.save(cliente));
    }

    @Transactional
    public void excluir(Long id) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Cliente cliente = repository.findByIdClienteAndIdOrganizacao(id, idOrganizacao)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));
        repository.delete(cliente);
    }

    @Transactional
    public Cliente registrarClienteAPartirDoOrcamento(Orcamento orcamento) {
        Long idOrganizacao = tenantContextService.idOrganizacaoObrigatoria();
        Cliente entrada = orcamento.getCliente();
        if (entrada == null || entrada.getNuCpfcnpj() == null || entrada.getNuCpfcnpj().isBlank()) {
            throw new ConflictException("CPF/CNPJ do cliente e obrigatorio");
        }

        Cliente cliente = repository.findByNuCpfcnpjAndIdOrganizacao(entrada.getNuCpfcnpj(), idOrganizacao)
                .orElseGet(Cliente::new);

        cliente.setIdOrganizacao(idOrganizacao);
        cliente.setTpCliente(entrada.getNuCpfcnpj().length() > 11 ? TipoCliente.Juridico : TipoCliente.Fisico);
        cliente.setNuCpfcnpj(entrada.getNuCpfcnpj());
        cliente.setNmCliente(entrada.getNmCliente());
        cliente.setNuTelefone(entrada.getNuTelefone());
        cliente.setDsEmail(entrada.getDsEmail());
        cliente.setDsObservacoes(entrada.getDsObservacoes());

        cliente = repository.save(cliente);
        orcamento.setCliente(cliente);
        orcamento.setIdCliente(cliente.getIdCliente());
        return cliente;
    }

    private void aplicar(Cliente cliente, ClienteRequest request, Long idOrganizacao) {
        cliente.setIdOrganizacao(idOrganizacao);
        cliente.setTpCliente(request.getTpCliente());
        cliente.setNuCpfcnpj(request.getNuCpfcnpj());
        cliente.setNmCliente(request.getNmCliente());
        cliente.setDsEmail(request.getDsEmail());
        cliente.setNuTelefone(request.getNuTelefone());
        cliente.setNuCep(request.getNuCep());
        cliente.setDsLogradouro(request.getDsLogradouro());
        cliente.setDsComplemento(request.getDsComplemento());
        cliente.setDsBairro(request.getDsBairro());
        cliente.setDsCidade(request.getDsCidade());
        cliente.setDsEstado(request.getDsEstado());
        if (request.getFlAtivo() != null) {
            cliente.setFlAtivo(request.getFlAtivo());
        }
        cliente.setDsObservacoes(request.getDsObservacoes());
    }

    private void validarDuplicidade(String nuCpfcnpj, Long idOrganizacao, Long idAtual) {
        Optional<Cliente> existente = repository.findByNuCpfcnpjAndIdOrganizacao(nuCpfcnpj, idOrganizacao);
        if (existente.isPresent() && !existente.get().getIdCliente().equals(idAtual)) {
            throw new ConflictException("CPF/CNPJ ja cadastrado para outro cliente");
        }
    }
}
