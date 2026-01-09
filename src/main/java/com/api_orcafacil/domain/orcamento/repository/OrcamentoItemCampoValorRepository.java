package com.api_orcafacil.domain.orcamento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.Orcamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItemCampoValor;
import com.api_orcafacil.domain.servico.model.Servico;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

import jakarta.transaction.Transactional;

public interface OrcamentoItemCampoValorRepository extends JpaRepository<OrcamentoItemCampoValor, Long> {

    @Query(value = "SELECT * FROM orcamento_item_campo_valor WHERE id_orcamentoitem = ?1", nativeQuery = true)
    List<OrcamentoItemCampoValor> findbyIdMestre(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM orcamento_item_campo_valor  WHERE id_orcamentoitem = ?1 ", nativeQuery = true)
    void deleteByIdMestre(Long id);

}