package com.api_orcafacil.repository.central;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.tenant.OrganizationStatus;
import com.api_orcafacil.tenant.central.model.CentralUsuarioOrganizacao;

public interface CentralUsuarioOrganizacaoRepository extends JpaRepository<CentralUsuarioOrganizacao, Long> {

    Optional<CentralUsuarioOrganizacao> findByIdUsuarioAndIdOrganizacao(Long idUsuario, Long idOrganizacao);

    boolean existsByIdOrganizacaoAndIdUsuarioAndFlAtivoTrue(Long idOrganizacao, Long idUsuario);

    @Query("""
            select uo from CentralUsuarioOrganizacao uo
            join fetch uo.usuario ug
            where uo.idOrganizacao = :idOrganizacao
              and uo.flAtivo = true
              and ug.flAtivo = true
            order by ug.nmUsuario
            """)
    List<CentralUsuarioOrganizacao> findVinculosAtivos(@Param("idOrganizacao") Long idOrganizacao);

    @Query("""
            select uo from CentralUsuarioOrganizacao uo
            join fetch uo.organizacao o
            where uo.idUsuario = :idUsuario
              and uo.flAtivo = true
              and o.flAtivo = true
              and o.status = :status
            order by o.nmOrganizacao
            """)
    List<CentralUsuarioOrganizacao> findOrganizacoesAtivasPorUsuario(
            @Param("idUsuario") Long idUsuario,
            @Param("status") OrganizationStatus status);

    @Query("""
            select uo from CentralUsuarioOrganizacao uo
            join fetch uo.organizacao o
            where uo.idUsuario = :idUsuario
              and uo.idOrganizacao = :idOrganizacao
              and uo.flAtivo = true
              and o.flAtivo = true
              and o.status = :status
            """)
    Optional<CentralUsuarioOrganizacao> findVinculoAtivo(
            @Param("idUsuario") Long idUsuario,
            @Param("idOrganizacao") Long idOrganizacao,
            @Param("status") OrganizationStatus status);

    long countByIdOrganizacaoAndFlAtivoTrue(Long idOrganizacao);
}
