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
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_empresa")
    @SequenceGenerator(name = "seq_empresa", sequenceName = "seq_empresa", allocationSize = 1)
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @NotBlank(message = "O tenant é obrigatorio!")
    @Column(name = "id_tenant")
    private String idTenant;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "cd_empresa")
    private String cdEmpresa;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_empresa")
    private String nmEmpresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planoassinatura", insertable = false, updatable = false)
    @JsonIgnore
    private PlanoAssinatura planoassinatura;

    @Column(name = "id_planoassinatura")
    private Long idPlanoAssinatura;

    @Column(name = "ds_email")
    private String dsEmail;

    @Column(name = "nu_telefone")
    private String nuTelefone;

    private String accessToken;

    private String webhookUrl;

    @Column(name = "fl_ativo")
    private boolean flAtivo = true;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }


    @JsonProperty("nmPlanoassinatura")
    public String getNmPlanoassinatura() {

        if (planoassinatura != null) {
            return planoassinatura.getNmPlanoAssinatura();
        }
        return null;    
    }

   

}
