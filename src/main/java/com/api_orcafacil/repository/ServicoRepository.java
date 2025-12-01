package com.api_orcafacil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.model.CategoriaServico;
import com.api_orcafacil.model.Servico;
import com.api_orcafacil.model.empresa.Empresa;
import com.api_orcafacil.repository.base.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ServicoRepository extends BaseRepository<Servico, Long> {

    @Query(value = "SELECT CASE WHEN MAX(c.cd_servico) IS NULL THEN '0' ELSE MAX(c.cd_servico) END FROM servico c ", nativeQuery = true)
    Long obterSequencial();

    @Query(value = "SELECT *  FROM servico b WHERE b.cd_servico = ?1 limit 1  ", nativeQuery = true)
    Optional<Servico> verificarCodigoExistente(String codigo);


}
