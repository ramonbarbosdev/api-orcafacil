package com.api_orcafacil.service.empresa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_orcafacil.model.empresa.PlanoAssinatura;
import com.api_orcafacil.repository.empresa.PlanoAssinaturaRepository;


@Service
public class PlanoAssinaturaService {

    @Autowired
    private PlanoAssinaturaRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public PlanoAssinatura salvar(PlanoAssinatura objeto) throws Exception {

        objeto = repository.save(objeto);

        return objeto;
    }


}
