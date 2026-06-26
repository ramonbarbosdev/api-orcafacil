package com.api_orcafacil.repository.central;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.tenant.central.model.CentralPapelPermissaoPadrao;
import com.api_orcafacil.tenant.central.model.CentralPapelPermissaoPadraoId;

public interface CentralPapelPermissaoPadraoRepository
        extends JpaRepository<CentralPapelPermissaoPadrao, CentralPapelPermissaoPadraoId> {

    void deleteByIdPapel(Long idPapel);

    long countByIdPapel(Long idPapel);

    @Query("""
            select pg.nmChave
            from CentralPapelPermissaoPadrao ppp
            join CentralPermissaoGlobal pg on pg.idPermissao = ppp.idPermissao
            where ppp.idPapel = :idPapel and pg.flAtivo = true
            order by pg.nmChave
            """)
    List<String> findChavesByIdPapel(@Param("idPapel") Long idPapel);

    @Query("""
            select pg.nmChave
            from CentralPapelPermissaoPadrao ppp
            join CentralPermissaoGlobal pg on pg.idPermissao = ppp.idPermissao
            join CentralPapel p on p.idPapel = ppp.idPapel
            where p.nmPapel = :nmPapel and p.flAtivo = true and pg.flAtivo = true
            order by pg.nmChave
            """)
    List<String> findChavesByNmPapel(@Param("nmPapel") String nmPapel);
}
