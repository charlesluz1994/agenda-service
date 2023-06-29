package cluz.com.agenda.api.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRequest {

    @NotBlank(message = "Patient name is required")
    private String name;

    @NotBlank(message = "Patient last name is required")
    private String lastname;

    @NotBlank(message = "Patient cpf is required")
    private String cpf;

    @NotBlank(message = "Patient email is required")
    @Email
    private String email;
}
