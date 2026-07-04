package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.model.OrcamentoNotificacaoEnviada;

public interface OrcamentoNotificacaoEnviadaRepository extends JpaRepository<OrcamentoNotificacaoEnviada, Long> {

    List<OrcamentoNotificacaoEnviada> findByIdOrcamentoAndIdOrganizacaoOrderByDtCriacaoDesc(
            Long idOrcamento, Long idOrganizacao);
}
