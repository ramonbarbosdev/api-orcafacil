package com.api_orcafacil.domain.empresa.model;

import java.beans.Transient;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @NotBlank(message = "O codigo é obrigatorio!")
    @Column(name = "cd_metodoprecificacao")
    private String cdMetodoPrecificacao;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_metodoprecificacao")
    private String nmMetodoPrecificacao;

    @Column(name = "ds_metodoprecificacao")
    private String dsMetodoPrecificacao;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

    public MetodoPrecificacao orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }

}
