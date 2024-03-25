package cluz.com.agenda.api.request;

import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRequest {

    @NotEmpty(message = "name cannot be empty")
    @NotBlank(message = "Patient name cannot be blank")
    private String name;

    @NotEmpty(message = "name cannot be empty")
    @NotBlank(message = "Patient last name cannot be blank")
    private String lastname;

    @NotEmpty(message = "name cannot be empty")
    @NotBlank(message = "Patient cpf is required")
    @CPF(message = "CPF is not following the right pattern. ie: 93922590821")
    private String cpf;

    @NotBlank(message = "Patient email cannot be blank")
    @Email
    private String email;
}
