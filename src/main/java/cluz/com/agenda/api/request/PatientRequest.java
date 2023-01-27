package cluz.com.agenda.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequest {

	@NotBlank(message="Patient name is required")
	private String nome;

	@NotBlank(message="Patient last name is required")
	private String sobrenome;

	@NotBlank(message="Patient cpf is required")
	private String cpf;

	@NotBlank(message="Patient email is required")
	@Email
	private String email;
}
