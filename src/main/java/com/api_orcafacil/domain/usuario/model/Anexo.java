package com.api_orcafacil.domain.usuario.model;


import java.time.LocalDateTime;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "anexo")
public class Anexo {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_anexo")
	@SequenceGenerator(name = "seq_anexo", sequenceName = "seq_anexo", allocationSize = 1)
	@Column(name = "id_anexo")
	private Long idAnexo;

	@NotNull(message = "O identificador modulo é obrigatorio!")
	@Column(name = "id_model")
	private Long idModel;

	@Column(name = "id_detalhe")
	private Long idDetalhe;

	@NotBlank(message = "O nome modulo é obrigatorio!")
	@Column(name = "nm_model")
	private String nmModel;

	@NotBlank(message = "O nome anexo é obrigatorio!")
	@Column(name = "nm_anexo")
	private String nmAnexo;

	@NotBlank(message = "A url  é obrigatorio!")
	private String url;

	@NotBlank(message = "O type é obrigatorio!")
	@Column(name = "content_type")
	private String contentType;

	@NotNull(message = "O tamanho é obrigatorio!")
	@Column(name = "nu_tamanho")
	private Long nuTamanho;

	@Column(name = "dt_cadastro", nullable = false, updatable = false)
	private LocalDateTime dtCadastro;

	@PrePersist
	protected void onCreate() {
		this.dtCadastro = LocalDateTime.now();
	}

}
