package com.api_orcafacil.repository.central;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralOrganizacaoConsumo;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoConsumoId;

public interface CentralOrganizacaoConsumoRepository
        extends JpaRepository<CentralOrganizacaoConsumo, CentralOrganizacaoConsumoId> {

    Optional<CentralOrganizacaoConsumo> findByIdOrganizacaoAndNmChaveLimiteAndDtReferencia(
            Long idOrganizacao, String nmChaveLimite, LocalDate dtReferencia);
}
