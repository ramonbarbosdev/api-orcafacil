package com.api_orcafacil.tenant.central.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_limite")
public class CentralTipoLimite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipolimite")
    private Long idTipoLimite;

    @Column(name = "nm_chave", nullable = false, unique = true, length = 80)
    private String nmChave;

    @Column(name = "nm_limite", nullable = false)
    private String nmLimite;

    @Column(name = "ds_limite", columnDefinition = "text")
    private String dsLimite;

    @Column(name = "tp_limite", nullable = false, length = 30)
    private String tpLimite = "CONTAGEM";

    @Column(name = "fl_ativo", nullable = false)
    private boolean flAtivo = true;
}
