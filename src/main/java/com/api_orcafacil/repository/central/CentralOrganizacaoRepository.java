package com.api_orcafacil.repository.central;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralOrganizacao;

public interface CentralOrganizacaoRepository extends JpaRepository<CentralOrganizacao, Long> {

    boolean existsBySlugOrDatabaseName(String slug, String databaseName);

    boolean existsByIdOrganizacao(Long idOrganizacao);
}
