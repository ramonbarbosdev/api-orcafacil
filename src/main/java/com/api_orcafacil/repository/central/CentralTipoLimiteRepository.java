package com.api_orcafacil.repository.central;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralTipoLimite;

public interface CentralTipoLimiteRepository extends JpaRepository<CentralTipoLimite, Long> {

    List<CentralTipoLimite> findByFlAtivoTrueOrderByNmLimiteAsc();
}
