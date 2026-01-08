package com.api_orcafacil.domain.catalogo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.domain.catalogo.model.Catalogo;
import com.api_orcafacil.domain.orcamento.model.CodicaoPagamento;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

public interface CatalogoRepository extends BaseRepository<Catalogo, Long> {


    @Query(value = "SELECT CASE WHEN MAX(c.cd_catalogo) IS NULL THEN '0' ELSE MAX(c.cd_catalogo) END FROM catalogo c ", nativeQuery = true)
    Long obterSequencial();

    @Query(value = "SELECT *  FROM catalogo b WHERE b.cd_catalogo = ?1 limit 1  ", nativeQuery = true)
    Optional<Catalogo> verificarCodigoExistente(String codigo, String idTenant);

}