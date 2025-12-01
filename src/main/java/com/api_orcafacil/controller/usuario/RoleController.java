package com.api_orcafacil.controller.usuario;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_orcafacil.model.usuario.Role;
import com.api_orcafacil.model.usuario.Usuario;
import com.api_orcafacil.repository.usuario.RoleRepository;
import com.api_orcafacil.repository.usuario.UsuarioRepository;
import com.api_orcafacil.service.usuario.RoleService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/role")
@Tag(name = "Role")
public class RoleController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService service;


    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<?> criarNovaRole(@RequestBody Role objeto) throws Exception {
        service.salvar(objeto);
        return new ResponseEntity<>(Map.of("message", "Registro salvo com sucesso"), HttpStatus.CREATED);

    }

    @PutMapping(value = "/atualizar-role/{id_usuario}/{id_role}", produces = "application/json")
    public ResponseEntity<?> atualizarRole(@PathVariable Long id_usuario, @PathVariable Long id_role) throws Exception {

        Optional<Role> roleUser = roleRepository.findById(id_role);
        Optional<Usuario> usuario = usuarioRepository.findById(id_usuario);

        if (usuario.isPresent()) {
            for (Role role : usuario.get().getRoles()) {

                if (!role.getId().equals(id_role)) {
                    throw new Exception(
                            "O usuario " + usuario.get().getNome() + " já possui um papel de " + role.getNomeRole());
                }

                if (role.getId().equals(id_role)) {
                    throw new Exception("Esse papel já está incluso no usuario " + usuario.get().getNome());
                }

            }
        }

        usuario.get().getRoles().add(roleUser.get());

        usuarioRepository.save(usuario.get());

        return new ResponseEntity<>(Map.of("message", "Acesso incluido para o usuario " + usuario.get().getNome()),
                HttpStatus.OK);
    }

    @GetMapping(value = "/obter-por-usuario/{id_usuario}", produces = "application/json")
    public ResponseEntity<?> obterRolesUsuario(@PathVariable Long id_usuario) {

        Optional<Usuario> usuario = usuarioRepository.findById(id_usuario);

        return new ResponseEntity<>(usuario.get().getRoles(), HttpStatus.OK);

    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<?> obterTotasRoles() {

        List<Role> object = (List<Role>) roleRepository.findAll();

        return new ResponseEntity<>(object, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> obterRoleId(@PathVariable Long id) {

        Optional<Role> optionalRole = roleRepository.findById(id);

        return ResponseEntity.ok(optionalRole.get());
    }

    @DeleteMapping(value = "/remover-por-usuario/{id_usuario}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removerRolesUsuario(@PathVariable Long id_usuario) {

        roleRepository.deleteByUsuarioId(id_usuario);

        return new ResponseEntity<>(Map.of("message", "Acesso removido do usuario!"), HttpStatus.OK);

    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deletar(@PathVariable Long id) {

        service.excluir(id);

		return ResponseEntity.ok(Map.of("message", "Removido com sucesso!"));

    }
}
