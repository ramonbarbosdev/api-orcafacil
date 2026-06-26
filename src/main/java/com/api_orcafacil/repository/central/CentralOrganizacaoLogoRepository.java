package com.api_orcafacil.repository.central;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralOrganizacaoLogo;

public interface CentralOrganizacaoLogoRepository extends JpaRepository<CentralOrganizacaoLogo, Long> {

    Optional<CentralOrganizacaoLogo> findByIdOrganizacaoAndFlAtivoTrue(Long idOrganizacao);
}
