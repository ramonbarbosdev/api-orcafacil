package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.Cliente;

public interface ClienteRepository extends TenantRepository<Cliente> {

    Optional<Cliente> findByIdClienteAndIdOrganizacao(Long idCliente, Long idOrganizacao);

    @Query(value = "SELECT * FROM cliente b WHERE b.nu_cpfcnpj = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Cliente> findByNuCpfcnpjAndIdOrganizacao(String nuCpfcnpj, Long idOrganizacao);

    long countByIdOrganizacao(Long idOrganizacao);
}
