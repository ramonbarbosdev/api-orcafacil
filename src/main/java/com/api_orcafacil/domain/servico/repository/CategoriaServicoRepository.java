package com.api_orcafacil.domain.servico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.servico.model.CategoriaServico;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CategoriaServicoRepository extends BaseRepository<CategoriaServico, Long> {

    @Query(value = "SELECT CASE WHEN MAX(c.cd_categoriaservico) IS NULL THEN '0' ELSE MAX(c.cd_categoriaservico) END FROM categoria_servico c ", nativeQuery = true)
    Long obterSequencial();

    @Query(value = "SELECT *  FROM categoria_servico b WHERE b.cd_categoriaservico = ?1 limit 1  ", nativeQuery = true)
    Optional<CategoriaServico> verificarCodigoExistente(String codigo);


}
