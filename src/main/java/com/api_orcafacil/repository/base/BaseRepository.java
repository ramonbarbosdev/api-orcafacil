package com.api_orcafacil.repository.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.api_orcafacil.model.Cliente;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    Page<T> findByIdTenant(String idTenant, Pageable pageable);
        List<T> findAllByIdTenant(String idTenant);

}