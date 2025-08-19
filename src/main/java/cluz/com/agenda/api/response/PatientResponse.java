package cluz.com.agenda.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
