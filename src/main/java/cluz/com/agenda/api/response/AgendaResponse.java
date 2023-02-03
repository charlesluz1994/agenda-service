package cluz.com.agenda.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AgendaResponse {
	private Long id;
	private String description;
	private LocalDateTime appointment_time;
	private PatientResponse patient;
}
