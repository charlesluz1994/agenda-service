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
	private LocalDateTime appointmentTime;
	private LocalDateTime createdDate;
	private Long patientId;

	private String patientName;
	private String patientEmail;
}
