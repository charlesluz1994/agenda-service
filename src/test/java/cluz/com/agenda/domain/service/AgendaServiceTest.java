package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.AgendaRepository;
import cluz.com.agenda.exception.BusinessException;
import cluz.com.agenda.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {
	@Mock
	AgendaRepository repository;

	@Mock
	PatientService patientService;

	@InjectMocks
	AgendaService agendaService;

	@Captor
	ArgumentCaptor<Agenda> agendaCaptor;

	@Test
	@DisplayName("Given a Agenda when Agenda is valid then save Agenda successfully")
	void givenSaveAgenda_whenAgendaIsValid_thenReturnAgendaSuccessfully() {
		// Given
		Agenda expectedAgenda = buildAgenda();
		when(patientService.findPatientById(expectedAgenda.getPatient().getId())).thenReturn(expectedAgenda.getPatient());
		when(repository.save(expectedAgenda)).thenReturn(expectedAgenda);

		// When
		agendaService.save(expectedAgenda);

		// Then
		verify(patientService).findPatientById(expectedAgenda.getPatient().getId());
		verify(repository).save(agendaCaptor.capture());
		Agenda savedAgenda = agendaCaptor.getValue();

		assertEquals(expectedAgenda, savedAgenda, "agenda should match");
		assertEquals(expectedAgenda.getPatient(), savedAgenda.getPatient(), "patient should match");
		assertNotNull(savedAgenda.getCreatedDate(), "Create date of agenda cannot be null");
		assertNotNull(savedAgenda.getPatient(), "Patient cannot be null");
	}

	@Test
	@DisplayName("Given a Agenda when Patient is not valid then Throw NotFoundException")
	void givenAgenda_whenPatientIsNotValid_thenThrowNotFoundException() {
		// Given
		Agenda invalidAgenda = buildInvalidAgenda();

		// When & Then
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			agendaService.save(invalidAgenda);
		});

		assertEquals("This patient is not registered.!", exception.getMessage(), "Exception message should match");
	}

	@Test
	@DisplayName("Given a Agenda when time already scheduled then Throw BusinessException")
	void givenAgenda_whenTimeAlreadyScheduled_thenThrowBusinessException() {
		// Given
		Agenda expectedAgenda = buildExistingAgenda();
		Agenda conflictingAgenda = buildAgenda();

		when(patientService.findPatientById(expectedAgenda.getPatient().getId()))
				.thenReturn(expectedAgenda.getPatient());

		when(repository.findByPatientId(expectedAgenda.getPatient().getId()))
				.thenReturn(List.of(expectedAgenda));

		// When
		// Then
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			agendaService.save(conflictingAgenda);
		});

		assertEquals("Time already scheduled!", exception.getMessage(), "exception message should match");

		verify(patientService).findPatientById(expectedAgenda.getPatient().getId());
		verify(repository).findByPatientId(expectedAgenda.getPatient().getId());
	}


	private Agenda buildAgenda() {
		Patient patient = buildPatient();
		return Agenda.builder()
				.id(1L)
				.patient(patient)
				.description("Charles Appointment - Dentist")
				.appointmentTime(LocalDateTime.now().plusDays(1))
				.createdDate(LocalDateTime.now())
				.build();
	}

	private Agenda buildExistingAgenda() {
		Patient patient = buildPatient();
		return Agenda.builder()
				.id(2L)
				.patient(patient)
				.description("Existing Appointment")
				.appointmentTime(LocalDateTime.now().plusDays(1))
				.createdDate(LocalDateTime.now().minusDays(1))
				.build();
	}

	private Patient buildPatient() {
		return Patient.builder()
				.id(1L)
				.name("Charles")
				.cpf("428282000111")
				.agendas(new ArrayList<>())
				.build();
	}

	private Patient buildInvalidPatient() {
		return Patient.builder()
				.id(null)
				.name("Charles")
				.cpf("428282000111")
				.agendas(new ArrayList<>())
				.build();
	}

	private Agenda buildInvalidAgenda() {
		Patient patient = buildInvalidPatient();
		return Agenda.builder()
				.id(2L)
				.patient(patient)
				.description("Existing Appointment")
				.appointmentTime(LocalDateTime.now().plusDays(1))
				.createdDate(LocalDateTime.now().minusDays(1))
				.build();
	}
}