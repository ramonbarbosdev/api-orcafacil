package com.api_orcafacil.domain.empresa.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.api_orcafacil.domain.empresa.repository.EmpresaRepository;
import com.api_orcafacil.domain.sistema.service.ValidacaoService;
import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.domain.usuario.model.UsuarioEmpresa;
import com.api_orcafacil.util.TenantUtil;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    @Autowired
    private ValidacaoService validacaoService;

    public static final Function<Empresa, Long> ID_FUNCTION = Empresa::getIdEmpresa;

    public static final Function<Empresa, String> SEQUENCIA_FUNCTION = Empresa::getCdEmpresa;

    @Transactional(rollbackFor = Exception.class)
    public Empresa salvar(Empresa objeto) throws Exception {

        validarObjeto(objeto);

        return repository.save(objeto);
    }

    public void validarObjeto(Empresa objeto) throws Exception {
        
        validacaoService.validarCodigoExistente(
                ID_FUNCTION.apply(objeto),
                repository.verificarCodigoExistente(SEQUENCIA_FUNCTION.apply(objeto)),
                ID_FUNCTION);

        if (objeto.getIdTenant() == null || objeto.getIdTenant().isEmpty()) {
            objeto.setIdTenant(TenantUtil.generateTenantId());
        }

        validarCdEmpresa(objeto.getCdEmpresa());
    }

    private void validarCdEmpresa(String cdEmpresa)
    {
        if (cdEmpresa == null || !cdEmpresa.matches("^(\\d{11}|\\d{14})$")) {
            throw new IllegalArgumentException(
                    "CNPJ/CPF deve conter 11 (CPF) ou 14 (CNPJ) dígitos numéricos");
        }
    }

    public Empresa buscarPorId(Long id) throws Exception {

        Optional<Empresa> objeto = repository.findById(id);

        return objeto.isPresent() ? objeto.get() : null;
    }

    public List<Empresa> buscarListagemVinculoPorUsuario(Long id_usuario) {
        List<Empresa> list = repository.buscarEmpresaPorTenantUsuario(id_usuario);
        return list == null ? Collections.emptyList() : list;

    }

    public Empresa verificarExistenciaPorNome(String nome) throws Exception {

        Optional<Empresa> objeto = repository.verificarNomeExistente(nome);

        return objeto.isPresent() ? objeto.get() : null;
    }

    public String sequencia() throws Exception {

        String sq_sequencia = validacaoService.gerarSequencia(repository.obterSequencial());

        return sq_sequencia;
    }
}
