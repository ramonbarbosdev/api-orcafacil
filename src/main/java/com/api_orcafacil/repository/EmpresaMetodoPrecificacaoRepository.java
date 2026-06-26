package com.api_orcafacil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.model.EmpresaMetodoPrecificacao;

public interface EmpresaMetodoPrecificacaoRepository extends TenantRepository<EmpresaMetodoPrecificacao> {

    @Query("""
            SELECT e FROM EmpresaMetodoPrecificacao e
            JOIN FETCH e.metodoPrecificacao
            WHERE e.idOrganizacao = :idOrganizacao
            """)
    List<EmpresaMetodoPrecificacao> findByIdOrganizacaoWithMetodo(@Param("idOrganizacao") Long idOrganizacao);

    @Query("""
            SELECT e FROM EmpresaMetodoPrecificacao e
            JOIN FETCH e.metodoPrecificacao
            WHERE e.idEmpresaMetodoPrecificacao = :id
              AND e.idOrganizacao = :idOrganizacao
            """)
    Optional<EmpresaMetodoPrecificacao> findByIdAndOrganizacaoWithMetodo(
            @Param("id") Long id,
            @Param("idOrganizacao") Long idOrganizacao);

    Optional<EmpresaMetodoPrecificacao> findByIdEmpresaMetodoPrecificacaoAndIdOrganizacao(Long id, Long idOrganizacao);

    Optional<EmpresaMetodoPrecificacao> findByIdOrganizacaoAndIdMetodoPrecificacao(Long idOrganizacao, Long idMetodoPrecificacao);

    @Query(value = "SELECT * FROM empresa_metodo_precificacao b WHERE b.id_metodoprecificacao = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<EmpresaMetodoPrecificacao> findByMetodoAndOrganizacao(Long idMetodoPrecificacao, Long idOrganizacao);
}
