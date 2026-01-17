package com.api_orcafacil.domain.orcamento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoStatusHistorico;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

import jakarta.transaction.Transactional;

public interface OrcamentoStatusHistoricoRepository extends BaseRepository<OrcamentoStatusHistorico, Long> {

  List<OrcamentoStatusHistorico> findByOrcamento_IdOrcamentoOrderByDtCadastroAsc(Long idOrcamento);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM orcamento_status_historico  WHERE id_orcamento = ?1 ", nativeQuery = true)
    void deleteByIdMestre(Long id);


}