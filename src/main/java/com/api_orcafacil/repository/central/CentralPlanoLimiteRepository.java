package com.api_orcafacil.repository.central;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.api_orcafacil.tenant.central.model.CentralPlanoLimite;
import com.api_orcafacil.tenant.central.model.CentralPlanoLimiteId;

public interface CentralPlanoLimiteRepository extends JpaRepository<CentralPlanoLimite, CentralPlanoLimiteId> {

    List<CentralPlanoLimite> findByIdPlanoAssinatura(Long idPlanoAssinatura);

    @Modifying
    @Query("DELETE FROM CentralPlanoLimite p WHERE p.idPlanoAssinatura = :idPlanoAssinatura")
    void deleteByIdPlanoAssinatura(Long idPlanoAssinatura);
}
