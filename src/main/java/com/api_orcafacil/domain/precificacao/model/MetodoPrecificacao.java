package com.api_orcafacil.domain.precificacao.model;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;

import com.api_orcafacil.enums.TipoCliente;
import com.api_orcafacil.enums.TipoPrecificacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "metodo_precificacao")
public class MetodoPrecificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_metodoprecificacao")
    @SequenceGenerator(name = "seq_metodoprecificacao", sequenceName = "seq_metodoprecificacao", allocationSize = 1)
    @Column(name = "id_metodoprecificacao")
    private Long idMetodoPrecificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "cd_metodoprecificacao")
    @NotNull(message = "O codigo é obrigatorio!")
    private TipoPrecificacao cdMetodoPrecificacao;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_metodoprecificacao")
    private String nmMetodoPrecificacao;

    @Column(name = "ds_metodoprecificacao")
    private String dsMetodoPrecificacao;

    @jakarta.persistence.Transient
    @JsonProperty("campos")
    private List<CampoMetodoDTO> campos;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}
