package com.api_orcafacil.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.common.StatusOrcamento;
import com.api_orcafacil.model.Orcamento;

public interface OrcamentoRepository extends TenantRepository<Orcamento> {

    long countByIdOrganizacao(Long idOrganizacao);

    List<Orcamento> findByIdOrganizacaoOrderByDtCriacaoDesc(Long idOrganizacao, Pageable pageable);

    @Query("""
            SELECT o FROM Orcamento o
            LEFT JOIN FETCH o.cliente
            WHERE o.idOrganizacao = :idOrg
            ORDER BY o.dtCriacao DESC
            """)
    List<Orcamento> findRecentesComCliente(@Param("idOrg") Long idOrg, Pageable pageable);

    @Query("SELECT o.tpStatus, COUNT(o) FROM Orcamento o WHERE o.idOrganizacao = :idOrg GROUP BY o.tpStatus")
    List<Object[]> contarPorStatus(@Param("idOrg") Long idOrg);

    @Query("""
            SELECT COALESCE(SUM(o.vlPrecoFinal), 0) FROM Orcamento o
            WHERE o.idOrganizacao = :idOrg
              AND o.tpStatus = :status
              AND YEAR(o.dtEmissao) = :ano
              AND MONTH(o.dtEmissao) = :mes
            """)
    BigDecimal somarFaturamentoPorMes(
            @Param("idOrg") Long idOrg,
            @Param("status") StatusOrcamento status,
            @Param("ano") int ano,
            @Param("mes") int mes);

    @Query("""
            SELECT COUNT(o) FROM Orcamento o
            WHERE o.idOrganizacao = :idOrg
              AND YEAR(o.dtEmissao) = :ano
              AND MONTH(o.dtEmissao) = :mes
            """)
    long contarPorMes(@Param("idOrg") Long idOrg, @Param("ano") int ano, @Param("mes") int mes);

    @Query("""
            SELECT YEAR(o.dtEmissao), MONTH(o.dtEmissao), COUNT(o),
                   COALESCE(SUM(CASE WHEN o.tpStatus = com.api_orcafacil.common.StatusOrcamento.APROVADO THEN o.vlPrecoFinal ELSE 0 END), 0)
            FROM Orcamento o
            WHERE o.idOrganizacao = :idOrg AND o.dtEmissao >= :inicio
            GROUP BY YEAR(o.dtEmissao), MONTH(o.dtEmissao)
            ORDER BY YEAR(o.dtEmissao), MONTH(o.dtEmissao)
            """)
    List<Object[]> agregarSerieMensal(@Param("idOrg") Long idOrg, @Param("inicio") LocalDate inicio);

    Optional<Orcamento> findByIdOrcamentoAndIdOrganizacao(Long idOrcamento, Long idOrganizacao);

    Optional<Orcamento> findByCdPublico(String cdPublico);

    @Query(value = """
            SELECT COALESCE(
              MAX(CAST(SUBSTRING(c.nu_orcamento FROM '[0-9]+') AS BIGINT)),
              0
            )
            FROM orcamento c
            WHERE c.id_organizacao = ?1
            """, nativeQuery = true)
    Long obterSequencial(Long idOrganizacao);

    @Query(value = "SELECT * FROM orcamento b WHERE b.nu_orcamento = ?1 AND b.id_organizacao = ?2 LIMIT 1", nativeQuery = true)
    Optional<Orcamento> findByNuOrcamentoAndIdOrganizacao(String nuOrcamento, Long idOrganizacao);

    boolean existsByIdCliente(Long idCliente);

    boolean existsByIdCondicaoPagamento(Long idCondicaoPagamento);
}
