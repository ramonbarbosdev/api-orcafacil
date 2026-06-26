package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.OrcamentoItem;

public interface OrcamentoItemRepository extends JpaRepository<OrcamentoItem, Long> {

    List<OrcamentoItem> findByOrcamento_IdOrcamento(Long idOrcamento);

    @Modifying
    @Query("DELETE FROM OrcamentoItem i WHERE i.orcamento.idOrcamento = :idOrcamento")
    void deleteByIdOrcamento(Long idOrcamento);
}
