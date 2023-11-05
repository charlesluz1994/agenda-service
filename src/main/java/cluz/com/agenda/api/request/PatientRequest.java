package cluz.com.agenda.api.request;

import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRequest {

    @NotNull
    @NotEmpty
    @NotBlank(message = "Patient name is required")
    private String name;

    @NotNull
    @NotEmpty
    @NotBlank(message = "Patient last name is required")
    private String lastname;

    @NotNull
    @NotEmpty
    @NotBlank(message = "Patient cpf is required")
    @CPF(message = "CPF is not following the right pattern. ie: 93922590821")
    private String cpf;

    @NotBlank(message = "Patient email is required")
    @Email
    private String email;
}
