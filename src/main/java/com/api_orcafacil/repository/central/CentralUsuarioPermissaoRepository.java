package com.api_orcafacil.repository.central;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.tenant.central.model.CentralUsuarioPermissao;
import com.api_orcafacil.tenant.central.model.CentralUsuarioPermissaoId;

public interface CentralUsuarioPermissaoRepository
        extends JpaRepository<CentralUsuarioPermissao, CentralUsuarioPermissaoId> {

    @Query("""
            select distinct pg.nmChave
            from CentralUsuarioPermissao up
            join CentralPermissaoGlobal pg on pg.idPermissao = up.idPermissao
            where up.idUsuario = :idUsuario
              and up.idOrganizacao = :idOrganizacao
              and pg.flAtivo = true
            order by pg.nmChave
            """)
    List<String> findChavesByUsuarioEOrganizacao(
            @Param("idUsuario") Long idUsuario,
            @Param("idOrganizacao") Long idOrganizacao);
}
