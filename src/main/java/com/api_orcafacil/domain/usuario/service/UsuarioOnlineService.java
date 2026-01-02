package com.api_orcafacil.domain.usuario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.api_orcafacil.domain.usuario.model.UsuarioOnline;
import com.api_orcafacil.domain.usuario.repository.UsuarioOnlineRepository;
import com.api_orcafacil.protection.UsuarioOnlineDetalhadoProjection;

@Service
public class UsuarioOnlineService {

    @Autowired
    private UsuarioOnlineRepository repository;

    public void adicionarUsuario(String username) {
        Optional<UsuarioOnline> entity = repository.findByLogin(username);

        UsuarioOnline objeto;
        if (entity.isPresent()) {
            objeto = entity.get();
            objeto.setDtUltimologin(LocalDateTime.now());
            objeto.setFlAtivo(true);
        } else {
            objeto = new UsuarioOnline(username);
        }

        repository.save(objeto);
    }

    /** Marca o usuário como offline (logout ou inatividade) */
    public void removerUsuario(String username) {
        repository.findByLogin(username).ifPresent(u -> {
            u.setFlAtivo(false);
            repository.save(u);
        });
    }

    /** Lista todos os usuários online */
    public List<UsuarioOnline> listarUsuariosOnline() {
        List<UsuarioOnline> lista = new ArrayList<>();
        repository.findAll().forEach(lista::add);
        return lista.stream()
                .filter(UsuarioOnline::getFlAtivo)
                .toList();
    }

    public List<UsuarioOnlineDetalhadoProjection> obterInformacoesUsuario() {
        List<UsuarioOnlineDetalhadoProjection> lista = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {

            lista = repository.obterInformacoesUsuario(auth.getName());
        }

        return lista;
    }

    /** Verifica se está online */
    public boolean isOnline(String username) {
        return repository.findByLogin(username)
                .map(UsuarioOnline::getFlAtivo)
                .orElse(false);
    }
}
