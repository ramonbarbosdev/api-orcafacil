package com.api_orcafacil.domain.servico.model;

import java.time.LocalDateTime;

import com.api_orcafacil.domain.usuario.model.Usuario;
import com.api_orcafacil.enums.TipoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "categoria_servico")
public class CategoriaServico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categoriaservico")
    @SequenceGenerator(name = "seq_categoriaservico", sequenceName = "seq_categoriaservico", allocationSize = 1)
    @Column(name = "id_categoriaservico")
    private Long idCategoriaservico;

    @NotBlank(message = "O código é obrigatorio!")
    @Column(name = "cd_categoriaservico")
    private String cdCategoriaservico;

    @NotBlank(message = "O tenant é obrigatorio!")
    @Column(name = "id_tenant", nullable = false)
    private String idTenant;

    @NotBlank(message = "O nome é obrigatorio!")
    @Column(name = "nm_categoriaservico")
    private String nmCategoriaservico;

    @Column(name = "ds_observacoes")
    private String dsObservacoes;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dtCadastro    ;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }


}