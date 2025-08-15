package cluz.com.agenda.api.response;

import cluz.com.agenda.domain.entity.Agenda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {
	private Long id;
	private String name;
	private String lastname;
	private String cpf;
	private String email;
	private List<Agenda> agendas;
}
