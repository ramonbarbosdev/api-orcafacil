package com.api_orcafacil.domain.usuario.model;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "rota_permission")
@SequenceGenerator(name = "seq_rotapermission", sequenceName = "seq_rotapermission", allocationSize = 1, initialValue = 1)
public class RotaPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rotapermission")
    private Long idRotapermission;

    @NotBlank(message = "A rota é obrigatoria!")
    private String path; 

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rota_permission_methods", joinColumns = @JoinColumn(name = "id_rotapermission"))
    @Column(name = "method")
    private Set<String> methods = new HashSet<>(); 

    @Column(name = "fl_permitido")
    private boolean flPermitido; 

    @ManyToOne()
    @JoinColumn(name = "id_role", insertable = false, updatable = false)
    private Role role;

    @Column(name = "id_role")
    private Long idRole;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro    ;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro  = LocalDateTime.now();
    }


}
