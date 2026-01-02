package com.api_orcafacil.domain.usuario.service;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.service.EmpresaService;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.domain.usuario.model.Role;
import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.domain.usuario.model.UsuarioEmpresa;
import com.api_orcafacil.domain.usuario.repository.RoleRepository;
import com.api_orcafacil.domain.usuario.repository.UsuarioEmpresaRepository;
import com.api_orcafacil.domain.usuario.repository.UsuarioRepository;
import com.api_orcafacil.enums.TipoRole;
import com.api_orcafacil.util.MestreDetalheUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UsuarioService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cadastra ou atualiza um usuário.
     * 
     * @param usuario Dados do usuário a serem salvos
     * @return usuário salvo
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Usuario salvar(Usuario objeto) throws Exception {

 
        List<UsuarioEmpresa> itensUsuarioEmpresas = objeto.getItensUsuarioEmpresa();
        objeto.setItensUsuarioEmpresa(null);

        Usuario usuarioExistente = repository.findUserByLogin(objeto.getLogin());

        if (usuarioExistente == null) {
            objeto.setSenha(passwordEncoder.encode(objeto.getSenha()));
        } else {
            String senhaInformada = objeto.getSenha();
            String senhaBanco = usuarioExistente.getSenha();

            if (senhaInformada.equals(senhaBanco)) {
                objeto.setSenha(senhaBanco);
            }
            else if (!passwordEncoder.matches(senhaInformada, senhaBanco)) {
                objeto.setSenha(passwordEncoder.encode(senhaInformada));
            }
            else {
                objeto.setSenha(senhaBanco);
            }

            objeto.setId(usuarioExistente.getId());
        }

        validarObjeto(objeto);
        objeto = repository.save(objeto);

        salvarUsuarioEmpresaDetalhe(objeto, itensUsuarioEmpresas);

        return repository.save(objeto);
    }

    public void validarObjeto(Usuario objeto) throws Exception {

        String nomeRole = objeto.getRoles().iterator().next().getNomeRole();
        Role roleUser = roleRepository.findByNomeRole(nomeRole);
        if (roleUser == null) {
            roleUser = new Role();
            roleUser.setNomeRole(nomeRole);
            roleRepository.save(roleUser);
        }

        objeto.getRoles().clear();
        objeto.getRoles().add(roleUser);

    }

    public void salvarUsuarioEmpresaDetalhe(Usuario objeto,
            List<UsuarioEmpresa> itens) throws Exception {

        Function<Usuario, Long> getIdFunctionMestre = Usuario::getId;
        Function<UsuarioEmpresa, Long> getIdFunction = UsuarioEmpresa::getIdUsuarioempresa;

        Long idMestre = getIdFunctionMestre.apply(objeto);

        MestreDetalheUtils.removerItensGenerico(
                idMestre,
                itens,
                usuarioEmpresaRepository::findbyIdMestre,
                usuarioEmpresaRepository::deleteById,
                getIdFunction);

        if (itens != null && itens.size() > 0) {
            for (UsuarioEmpresa item : itens) {
                item.setIdUsuario(idMestre);

                Long idExistente = getIdFunction.apply(item);

                if (idExistente == null || idExistente == 0) {
                    item.setIdUsuarioempresa(null);
                }

                validarItemUsuarioEmpresa(item, itens, objeto);

                item = usuarioEmpresaRepository.save(item);
            }

            objeto.setItensUsuarioEmpresa(itens);
        }
    }

    public void validarItemUsuarioEmpresa(UsuarioEmpresa item,
            List<UsuarioEmpresa> itens, Usuario objeto) throws Exception {

        if (item.getIdEmpresa() == null) {
            throw new Exception("A empresa vinculada não pode ser nula.");
        }

        boolean existeVinculoBanco = usuarioEmpresaRepository
                .existsByIdUsuarioAndIdEmpresa(objeto.getId(), item.getIdEmpresa());

        if (existeVinculoBanco && (item.getIdUsuarioempresa() == null || item.getIdUsuarioempresa() == 0)) {
            throw new Exception("O usuário já está vinculado a esta empresa.");
        }

        long countMesmoTenant = itens.stream()
                .filter(i -> i.getIdEmpresa() != null &&
                        i.getIdEmpresa().equals(item.getIdEmpresa()))
                .count();

        if (countMesmoTenant > 1) {
            throw new Exception(
                    "Duplicidade detectada: o usuário não pode estar vinculado duas vezes à mesma empresa.");
        }
    }

    public Boolean existsVinculoEmpresaByIdUsuarioAndByIdTenant(String id_tenant, Long id_usuario) {
        return usuarioEmpresaRepository.existsVinculoEmpresaByIdUsuarioAndByIdTenant(id_tenant, id_usuario);
    }

    public void inserirSenhaCriptografada(Usuario usuario, String novaSenha) throws Exception {

        if (novaSenha.isBlank()) {
            return;
        } else {
            String senhacriptografada = new BCryptPasswordEncoder().encode(novaSenha);
            usuario.setSenha(senhacriptografada);
        }

    }

    public void excluir(Long id) throws Exception {

        Usuario objeto = entityManager.getReference(Usuario.class, id);

        if (objeto.getRoles().iterator().next().getNomeRole().equals(TipoRole.ROLE_DEV.name())) {
            throw new Exception("Voce não tem permissão para excluir um desenvolvedor!");

        }
        usuarioEmpresaRepository.deleteByIdMestre(id);

        repository.deleteById(id);
    }

}
