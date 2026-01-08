package com.api_orcafacil.domain.catalogo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.domain.catalogo.model.Catalogo;
import com.api_orcafacil.domain.catalogo.model.CatalogoCampo;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.orcamento.model.OrcamentoItem;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

public interface CatalogoCampoRepository extends JpaRepository<CatalogoCampo, Long> {

     @Query(value = "SELECT * FROM catalogo_campo WHERE id_catalogo = ?1", nativeQuery = true)
    List<CatalogoCampo> findbyIdMestre(Long id);

    @Modifying
    @Query(value = "DELETE FROM catalogo_campo  WHERE id_catalogo = ?1 ", nativeQuery = true)
    void deleteByIdMestre(Long id);

}