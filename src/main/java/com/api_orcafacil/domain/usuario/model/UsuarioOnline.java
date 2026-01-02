package com.api_orcafacil.domain.usuario.model;


import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ForeignKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario_online")
public class UsuarioOnline {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuarioonline")
	@SequenceGenerator(name = "seq_usuarioonline", sequenceName = "seq_usuarioonline", allocationSize = 1)
	@Column(name = "id_usuarioonline")
	private Long idUsuarioonline;

	@NotBlank(message = "O login é obrigatorio!")
	private String login;

	@Column(name = "dt_ultimologin")
	private LocalDateTime dtUltimologin;

	@Column(name = "fl_ativo")
	private Boolean flAtivo = true;


    public UsuarioOnline(String login) {
        this.login = login;
        this.flAtivo = true;
        this.dtUltimologin	 = LocalDateTime.now();
    }

}
