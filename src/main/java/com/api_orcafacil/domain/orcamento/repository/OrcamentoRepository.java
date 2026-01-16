package com.api_orcafacil.domain.orcamento.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

public interface OrcamentoRepository extends BaseRepository<Orcamento, Long> {

    Optional<Orcamento> findByIdTenant(String idTenant);
    Optional<Orcamento> findByIdAndIdTenant(Long id , String idTenant);

    @Query(value = """
            SELECT COALESCE(
              MAX(CAST(SUBSTRING(c.nu_orcamento FROM '[0-9]+') AS BIGINT)),
              0
            )
            FROM orcamento c
            where c.id_tenant = ?1
            """, nativeQuery = true)
    Long obterSequencial(String idTenant);

    @Query(value = "SELECT *  FROM orcamento b WHERE b.nu_orcamento = ?1 and b.id_tenant = ?2 limit 1  ", nativeQuery = true)
    Optional<Orcamento> verificarCodigoExistente(String codigo, String idTenant);

}