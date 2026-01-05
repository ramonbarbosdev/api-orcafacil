package com.api_orcafacil.domain.empresa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.model.EmpresaMetodoPrecificacao;
import com.api_orcafacil.domain.empresa.model.MetodoPrecificacao;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface EmpresaMetodoPrecificacaoRepository extends JpaRepository<EmpresaMetodoPrecificacao, Long> {

        Optional <EmpresaMetodoPrecificacao> findByIdTenant(String idTenant);

}
