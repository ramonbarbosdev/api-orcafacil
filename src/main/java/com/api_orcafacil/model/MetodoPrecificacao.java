package com.api_orcafacil.model;

import java.util.List;

import com.api_orcafacil.common.TipoPrecificacao;
import com.api_orcafacil.dto.precificacao.CampoMetodoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "metodo_precificacao")
public class MetodoPrecificacao extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodoprecificacao")
    @SequenceGenerator(name = "seq_metodoprecificacao", sequenceName = "seq_metodoprecificacao", allocationSize = 1)
    @Column(name = "id_metodoprecificacao")
    private Long idMetodoPrecificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "cd_metodoprecificacao", nullable = false, length = 30)
    private TipoPrecificacao cdMetodoPrecificacao;

    @Column(name = "nm_metodoprecificacao", nullable = false)
    private String nmMetodoPrecificacao;

    @Column(name = "ds_metodoprecificacao", columnDefinition = "text")
    private String dsMetodoPrecificacao;

    @Transient
    @JsonProperty("campos")
    private List<CampoMetodoDTO> campos;
}
