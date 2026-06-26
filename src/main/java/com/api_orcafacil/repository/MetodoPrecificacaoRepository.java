package com.api_orcafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.model.MetodoPrecificacao;

public interface MetodoPrecificacaoRepository extends JpaRepository<MetodoPrecificacao, Long> {

    Optional<MetodoPrecificacao> findByCdMetodoPrecificacao(TipoPrecificacao cdMetodoPrecificacao);
}
