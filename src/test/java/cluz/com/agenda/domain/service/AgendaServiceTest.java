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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
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

		assertEquals("This patient is not registered.", exception.getMessage(), "Exception message should match");
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

	@Test
	@DisplayName("Given back-to-back appointments when saving then no conflict and save succeeds")
	void givenConsecutiveAppointments_whenSave_thenSavedSuccessfully() {
		// Given: an existing 30-min appointment immediately followed by the new one
		Patient patient = buildPatient();
		LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withNano(0);

		Agenda existing = Agenda.builder()
				.id(2L)
				.patient(patient)
				.description("Existing Appointment")
				.appointmentTime(existingStart)
				.createdDate(LocalDateTime.now())
				.build();

		Agenda candidate = Agenda.builder()
				.id(3L)
				.patient(patient)
				.description("Back-to-back Appointment")
				.appointmentTime(existingStart.plusMinutes(30))
				.createdDate(LocalDateTime.now())
				.build();

		when(patientService.findPatientById(patient.getId())).thenReturn(patient);
		when(repository.findByPatientId(patient.getId())).thenReturn(List.of(existing));
		when(repository.save(candidate)).thenReturn(candidate);

		// When
		Agenda saved = agendaService.save(candidate);

		// Then
		assertNotNull(saved, "consecutive appointment should be saved");
		verify(repository).save(candidate);
	}

	@Test
	@DisplayName("Given a patient id when getting today's agendas then query spans the whole day")
	void givenPatientId_whenGetTodayAgendas_thenQueriesFullDayRange() {
		// Given
		ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(repository.findTodayAgendasByPatientId(eq(1L), startCaptor.capture(), endCaptor.capture()))
				.thenReturn(List.of());

		// When
		agendaService.getTodayAgendas(1L);

		// Then
		LocalDate today = LocalDate.now();
		assertEquals(today.atStartOfDay(), startCaptor.getValue(), "start bound should be start of today");
		assertEquals(today.atTime(LocalTime.MAX), endCaptor.getValue(), "end bound should be end of today");
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