package com.api_orcafacil.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.api_orcafacil.exception.BusinessException;
import com.api_orcafacil.security.JwtAuthentication;
import com.api_orcafacil.tenant.TenantDescriptor;
import com.api_orcafacil.tenant.TenantRuntimeContext;

@Service
public class TenantContextService {

    public JwtAuthentication atual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            return jwtAuthentication;
        }
        throw new BusinessException("Usuario nao autenticado");
    }

    public Long idUsuario() {
        return atual().getIdUsuario();
    }

    public Long idOrganizacaoObrigatoria() {
        Long idOrganizacao = atual().getIdOrganizacao();
        if (idOrganizacao == null) {
            throw new BusinessException("Contexto de organizacao obrigatorio");
        }
        return idOrganizacao;
    }

    public TenantDescriptor tenantDescriptor() {
        TenantRuntimeContext.CurrentTenant currentTenant = TenantRuntimeContext.get();
        if (currentTenant == null || currentTenant.descriptor() == null) {
            throw new BusinessException("Contexto runtime de tenant nao configurado");
        }
        return currentTenant.descriptor();
    }
}
