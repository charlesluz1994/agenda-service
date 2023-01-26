package cluz.com.agenda.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "agenda")
@Getter
@Setter
public class Agenda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "descricao")
	private String descricao;

	@Column(name = "data_hora")
	private LocalDateTime horario;

	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;

	@ManyToOne
	private Patient paciente;


}
