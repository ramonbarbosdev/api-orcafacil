package com.api_orcafacil.repository.central;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.tenant.central.model.CentralOrganizacaoConsumo;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoConsumoId;

import jakarta.persistence.LockModeType;

public interface CentralOrganizacaoConsumoRepository
        extends JpaRepository<CentralOrganizacaoConsumo, CentralOrganizacaoConsumoId> {

    Optional<CentralOrganizacaoConsumo> findByIdOrganizacaoAndNmChaveLimiteAndDtReferencia(
            Long idOrganizacao, String nmChaveLimite, LocalDate dtReferencia);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c FROM CentralOrganizacaoConsumo c
            WHERE c.idOrganizacao = :idOrganizacao
              AND c.nmChaveLimite = :nmChaveLimite
              AND c.dtReferencia = :dtReferencia
            """)
    Optional<CentralOrganizacaoConsumo> findForUpdate(
            @Param("idOrganizacao") Long idOrganizacao,
            @Param("nmChaveLimite") String nmChaveLimite,
            @Param("dtReferencia") LocalDate dtReferencia);
}
