package cluz.com.agenda.domain.repository;

import cluz.com.agenda.config.JpaAuditingConfig;
import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration test against a real PostgreSQL instance provisioned by Testcontainers.
 * Flyway builds the {@code agenda_schema} so the queries run against the production schema.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
@Testcontainers
class PostgresqlTest {

	@Container
	static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRES::getUsername);
		registry.add("spring.datasource.password", POSTGRES::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
		registry.add("spring.flyway.enabled", () -> "true");
		registry.add("spring.flyway.schemas", () -> "agenda_schema");
		registry.add("spring.flyway.locations", () -> "classpath:db/migration");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
		registry.add("spring.jpa.properties.hibernate.default_schema", () -> "agenda_schema");
		registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
	}

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	AgendaRepository agendaRepository;

	@Test
	@DisplayName("Given appointments for a patient when finding by patient id then only that patient's appointments are returned")
	void givenAppointments_whenFindByPatientId_thenReturnsThem() {
		Patient patient = patientRepository.save(buildPatient("11144477735"));
		Agenda agenda = agendaRepository.save(buildAgenda(patient, LocalDateTime.now().plusDays(1).withNano(0)));

		List<Agenda> found = agendaRepository.findByPatientId(patient.getId());

		assertThat(found).extracting(Agenda::getId).containsExactly(agenda.getId());
		assertThat(found.get(0).getCreatedDate()).as("auditing should populate createdDate").isNotNull();
	}

	@Test
	@DisplayName("Given an appointment today when querying today's range then it is returned")
	void givenAppointmentToday_whenFindTodayAgendas_thenReturnsIt() {
		Patient patient = patientRepository.save(buildPatient("52998224725"));
		LocalDateTime today = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
		Agenda todayAgenda = agendaRepository.save(buildAgenda(patient, today));
		agendaRepository.save(buildAgenda(patient, today.plusDays(1)));

		List<Agenda> found = agendaRepository.findTodayAgendasByPatientId(
				patient.getId(),
				today.toLocalDate().atStartOfDay(),
				today.toLocalDate().atTime(LocalTime.MAX));

		assertThat(found).extracting(Agenda::getId).containsExactly(todayAgenda.getId());
	}

	private Patient buildPatient(String cpf) {
		return Patient.builder()
				.name("Charles")
				.lastname("Luz")
				.cpf(cpf)
				.email("charles@gmail.com")
				.build();
	}

	private Agenda buildAgenda(Patient patient, LocalDateTime appointmentTime) {
		return Agenda.builder()
				.description("Appointment")
				.appointmentTime(appointmentTime)
				.patient(patient)
				.build();
	}
}
