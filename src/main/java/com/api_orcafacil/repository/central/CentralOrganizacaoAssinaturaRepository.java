package com.api_orcafacil.repository.central;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.common.AssinaturaStatus;
import com.api_orcafacil.tenant.central.model.CentralOrganizacaoAssinatura;

public interface CentralOrganizacaoAssinaturaRepository extends JpaRepository<CentralOrganizacaoAssinatura, Long> {

    Optional<CentralOrganizacaoAssinatura> findFirstByIdOrganizacaoAndTpStatusInOrderByDtInicioDesc(
            Long idOrganizacao, List<AssinaturaStatus> status);

    List<CentralOrganizacaoAssinatura> findByIdOrganizacaoOrderByDtInicioDesc(Long idOrganizacao);

    long countByIdPlanoAssinatura(Long idPlanoAssinatura);
}
