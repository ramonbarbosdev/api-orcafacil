package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.OrcamentoItemCampoValor;

public interface OrcamentoItemCampoValorRepository extends JpaRepository<OrcamentoItemCampoValor, Long> {

    List<OrcamentoItemCampoValor> findByOrcamentoItem_IdOrcamentoItem(Long idOrcamentoItem);

    @Modifying
    @Query("DELETE FROM OrcamentoItemCampoValor c WHERE c.orcamentoItem.idOrcamentoItem = :idOrcamentoItem")
    void deleteByIdOrcamentoItem(Long idOrcamentoItem);
}
