package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.EmpresaMetodoPrecificacao;

public interface EmpresaMetodoPrecificacaoRepository extends TenantRepository<EmpresaMetodoPrecificacao> {

    Optional<EmpresaMetodoPrecificacao> findByIdEmpresaMetodoPrecificacaoAndIdOrganizacao(Long id, Long idOrganizacao);

    Optional<EmpresaMetodoPrecificacao> findByIdOrganizacaoAndIdMetodoPrecificacao(Long idOrganizacao, Long idMetodoPrecificacao);

    @Query(value = "SELECT * FROM empresa_metodo_precificacao b WHERE b.id_metodoprecificacao = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<EmpresaMetodoPrecificacao> findByMetodoAndOrganizacao(Long idMetodoPrecificacao, Long idOrganizacao);
}
