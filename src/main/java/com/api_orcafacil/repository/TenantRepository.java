package com.api_orcafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.api_orcafacil.model.TenantEntity;

@NoRepositoryBean
public interface TenantRepository<T extends TenantEntity> extends JpaRepository<T, Long> {

    List<T> findByIdOrganizacao(Long idOrganizacao);
}
