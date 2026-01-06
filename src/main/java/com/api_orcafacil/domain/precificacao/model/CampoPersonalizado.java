package com.api_orcafacil.domain.precificacao.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.empresa.model.Empresa;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campos_personalizados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampoPersonalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_campo_personalizado")
    @SequenceGenerator(name = "seq_campo_personalizado", sequenceName = "seq_campo_personalizado", allocationSize = 1)
    @Column(name = "id_campopersonalizado")
    private Long idCampoPersonalizado;

    @Column(name = "id_tenant", nullable = false)
    @NotNull(message = "O tenant é obrigatorio!")
    private String idTenant;

    @Column(name = "cd_campopersonalizado", nullable = false, length = 50)
    @NotBlank(message = "O código é obrigatorio!")
    private String cdCampoPersonalizado;

    @Column(name = "nm_campopersonalizado", nullable = false, length = 100)
    @NotBlank(message = "O nome é obrigatorio!")
    private String nmCampoPersonalizado;

    @Column(name = "tp_campopersonalizado", nullable = false, length = 20)
    @NotBlank(message = "O tipo é obrigatorio!")
    private String tpCampoPersonalizado; 

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }
}
