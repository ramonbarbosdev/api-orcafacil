package com.api_orcafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.CampoMetodoDTO;
import com.api_orcafacil.dto.precificacao.MetodoPrecificacaoResponse;
import com.api_orcafacil.exception.ResourceNotFoundException;
import com.api_orcafacil.model.MetodoPrecificacao;
import com.api_orcafacil.repository.MetodoPrecificacaoRepository;

@Service
public class MetodoPrecificacaoService {

    private final MetodoPrecificacaoRepository repository;

    public MetodoPrecificacaoService(MetodoPrecificacaoRepository repository) {
        this.repository = repository;
    }

    public List<MetodoPrecificacaoResponse> listar() {
        return repository.findAll().stream().map(this::montar).toList();
    }

    public MetodoPrecificacaoResponse buscar(Long id) {
        return montar(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Metodo nao encontrado")));
    }

    @Transactional
    public MetodoPrecificacao salvar(MetodoPrecificacao objeto) {
        return repository.save(objeto);
    }

    public MetodoPrecificacaoResponse montar(MetodoPrecificacao metodo) {
        return MetodoPrecificacaoResponse.from(metodo, obterCamposPorTipo(metodo.getCdMetodoPrecificacao()));
    }

    private List<CampoMetodoDTO> obterCamposPorTipo(TipoPrecificacao tipo) {
        return switch (tipo) {
            case MARKUP -> List.of(new CampoMetodoDTO("percentual", "Percentual de Markup", "NUMBER", true));
            case MARGEM -> List.of(new CampoMetodoDTO("percentual", "Percentual de Margem", "NUMBER", true));
            case FIXO -> List.of(new CampoMetodoDTO("valor", "Valor Fixo", "NUMBER", true));
            case SIMPLES -> List.of();
        };
    }
}
