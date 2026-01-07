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
    
    @Query(value = "SELECT CASE WHEN MAX(c.nu_orcamento) IS NULL THEN '0' ELSE MAX(c.nu_orcamento) END FROM orcamento c ", nativeQuery = true)
    Long obterSequencial();

    @Query(value = "SELECT *  FROM orcamento b WHERE b.nu_orcamento = ?1 limit 1  ", nativeQuery = true)
    Optional<Orcamento> verificarCodigoExistente(String codigo);

}