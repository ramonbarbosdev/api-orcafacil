package com.api_orcafacil.domain.empresa.model;


import java.time.LocalDateTime;

import com.api_orcafacil.domain.empresa.model.PlanoAssinatura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "plano_assinatura")
public class PlanoAssinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_planoassinatura")
    @SequenceGenerator(name = "seq_planoassinatura", sequenceName = "seq_planoassinatura", allocationSize = 1)
    @Column(name = "id_planoassinatura")
    private Long idPlanoAssinatura; 

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_planoassinatura")
    private String nmPlanoAssinatura;

    @Column(name = "vl_mensal")
    private Double vlMensal;

    @Column(name = "nu_limitemensagens")
    private int nuLimitemensagens;

    @Column(name = "nu_limiteatendentes")
    private int nuLimiteatendentes;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }


    

}
