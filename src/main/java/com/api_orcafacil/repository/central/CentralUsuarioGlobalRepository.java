package com.api_orcafacil.repository.central;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api_orcafacil.tenant.central.model.CentralUsuarioGlobal;

public interface CentralUsuarioGlobalRepository extends JpaRepository<CentralUsuarioGlobal, Long> {

    Optional<CentralUsuarioGlobal> findByNuCpfAndFlAtivoTrue(String nuCpf);

    Optional<CentralUsuarioGlobal> findByNuCpf(String nuCpf);

    @Query("""
            select ug from CentralUsuarioGlobal ug
            where ug.idUsuario = :idUsuario and ug.flAtivo = true
            """)
    Optional<CentralUsuarioGlobal> findAtivoById(@Param("idUsuario") Long idUsuario);
}
