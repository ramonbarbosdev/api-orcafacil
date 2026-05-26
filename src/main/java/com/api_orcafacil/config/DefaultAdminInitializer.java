package com.api_orcafacil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.service.EmpresaService;
import com.api_orcafacil.domain.usuario.model.Role;
import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.domain.usuario.model.UsuarioEmpresa;
import com.api_orcafacil.domain.usuario.repository.RoleRepository;
import com.api_orcafacil.domain.usuario.repository.UsuarioEmpresaRepository;
import com.api_orcafacil.domain.usuario.repository.UsuarioRepository;
import com.api_orcafacil.enums.TipoRole;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private static final String EMPRESA_BASE_NOME = "Administração";
    private static final String EMPRESA_BASE_CODIGO = "00000000000";

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final EmpresaService empresaService;
    private final PasswordEncoder passwordEncoder;

    @Value("${DEFAULT_ADMIN_ENABLED}")
    private boolean defaultAdminEnabled;

    @Value("${DEFAULT_ADMIN_LOGIN}")
    private String defaultAdminLogin;

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;

    @Value("${DEFAULT_ADMIN_NAME}")
    private String defaultAdminName;

    public DefaultAdminInitializer(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            EmpresaService empresaService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.empresaService = empresaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        if (!defaultAdminEnabled) {
            return;
        }

        if (usuarioRepository.findUserByLogin(defaultAdminLogin) != null) {
            return;
        }

        Role roleAdmin = obterOuCriarRoleAdmin();
        Empresa empresaBase = obterOuCriarEmpresaBase();
        Usuario usuarioAdmin = obterOuCriarUsuarioAdmin(roleAdmin);

        vincularUsuarioEmpresa(usuarioAdmin, empresaBase);
    }

    private Role obterOuCriarRoleAdmin() {
        Role roleAdmin = roleRepository.findByNomeRole(TipoRole.ROLE_ADMIN.name());

        if (roleAdmin == null) {
            roleAdmin = new Role();
            roleAdmin.setNomeRole(TipoRole.ROLE_ADMIN.name());
            roleAdmin = roleRepository.save(roleAdmin);
        }

        return roleAdmin;
    }

    private Empresa obterOuCriarEmpresaBase() throws Exception {
        Empresa empresa = empresaService.verificarExistenciaPorNome(EMPRESA_BASE_NOME);

        if (empresa == null) {
            empresa = new Empresa();
            empresa.setNmEmpresa(EMPRESA_BASE_NOME);
            empresa.setCdEmpresa(EMPRESA_BASE_CODIGO);
            empresa.setFlAtivo(true);
            empresa = empresaService.salvar(empresa);
        }

        return empresa;
    }

    private Usuario obterOuCriarUsuarioAdmin(Role roleAdmin) {
        Usuario usuario = usuarioRepository.findUserByLogin(defaultAdminLogin);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setLogin(defaultAdminLogin);
            usuario.setNome(defaultAdminName);
            usuario.setSenha(passwordEncoder.encode(defaultAdminPassword));
        }

        usuario.getRoles().clear();
        usuario.getRoles().add(roleAdmin);

        return usuarioRepository.save(usuario);
    }

    private void vincularUsuarioEmpresa(Usuario usuario, Empresa empresa) {
        boolean existeVinculo = usuarioEmpresaRepository.existsByIdUsuarioAndIdEmpresa(
                usuario.getId(),
                empresa.getIdEmpresa());

        if (existeVinculo) {
            return;
        }

        
        UsuarioEmpresa usuarioEmpresa = new UsuarioEmpresa();
        usuarioEmpresa.setIdUsuario(usuario.getId());
        usuarioEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        usuarioEmpresaRepository.save(usuarioEmpresa);
    }
}
