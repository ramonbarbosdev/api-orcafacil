package com.api_orcafacil.repository.central;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_orcafacil.tenant.central.model.CentralPermissaoGlobal;

public interface CentralPermissaoGlobalRepository extends JpaRepository<CentralPermissaoGlobal, Long> {

    List<CentralPermissaoGlobal> findAllByOrderByNmChaveAsc();

    List<CentralPermissaoGlobal> findByFlAtivoTrueOrderByNmChaveAsc();

    List<CentralPermissaoGlobal> findByNmChaveInAndFlAtivoTrueOrderByIdPermissaoAsc(Collection<String> chaves);

    List<CentralPermissaoGlobal> findByNmChaveStartingWith(String prefix);

    boolean existsByNmChaveStartingWith(String prefix);

    java.util.Optional<CentralPermissaoGlobal> findByNmChave(String nmChave);

    boolean existsByNmChave(String nmChave);
}
