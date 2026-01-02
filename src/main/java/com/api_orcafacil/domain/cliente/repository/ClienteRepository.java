package com.api_orcafacil.domain.cliente.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api_orcafacil.domain.cliente.model.Cliente;
import com.api_orcafacil.domain.sistema.repository.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ClienteRepository extends BaseRepository<Cliente, Long> {

    @Query(value = "SELECT *  FROM cliente b WHERE b.nu_cpfcnpj = ?1 limit 1  ", nativeQuery = true)
    Optional<Cliente> verificarCodigoExistente(String codigo);


}