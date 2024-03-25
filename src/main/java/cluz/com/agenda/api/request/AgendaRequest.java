package cluz.com.agenda.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgendaRequest {

    @NotNull(message = "description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Appointment time cannot be null")
    @Future(message = "Time cannot be less than now.")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Patient is cannot be null")
    private Long patientId;
}