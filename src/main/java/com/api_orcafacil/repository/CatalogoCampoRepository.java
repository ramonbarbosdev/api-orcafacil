package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.model.CatalogoCampo;

public interface CatalogoCampoRepository extends JpaRepository<CatalogoCampo, Long> {

    List<CatalogoCampo> findByCatalogo_IdCatalogo(Long idCatalogo);

    @Modifying
    @Query("DELETE FROM CatalogoCampo c WHERE c.catalogo.idCatalogo = :idCatalogo")
    void deleteByIdCatalogo(Long idCatalogo);
}
