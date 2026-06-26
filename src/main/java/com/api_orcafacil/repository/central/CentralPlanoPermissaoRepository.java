package com.api_orcafacil.repository.central;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.tenant.central.model.CentralPlanoPermissao;
import com.api_orcafacil.tenant.central.model.CentralPlanoPermissaoId;

public interface CentralPlanoPermissaoRepository
        extends JpaRepository<CentralPlanoPermissao, CentralPlanoPermissaoId> {

    void deleteByIdPlanoAssinatura(Long idPlanoAssinatura);

    @Query("""
            select pg.nmChave
            from CentralPlanoPermissao pp
            join CentralPermissaoGlobal pg on pg.idPermissao = pp.idPermissao
            where pp.idPlanoAssinatura = :idPlano and pg.flAtivo = true
            order by pg.nmChave
            """)
    List<String> findChavesByIdPlanoAssinatura(@Param("idPlano") Long idPlanoAssinatura);
}
