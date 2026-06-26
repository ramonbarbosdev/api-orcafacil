package com.api_orcafacil.repository.central;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralPapel;

public interface CentralPapelRepository extends JpaRepository<CentralPapel, Long> {

    List<CentralPapel> findByFlAtivoTrueOrderByNmPapelAsc();

    Optional<CentralPapel> findByIdPapelAndFlAtivoTrue(Long idPapel);

    boolean existsByIdPapelAndFlAtivoTrue(Long idPapel);
}
