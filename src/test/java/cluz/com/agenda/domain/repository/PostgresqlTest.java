package cluz.com.agenda.domain.repository;

import cluz.com.agenda.domain.entity.Agenda;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class PostgresqlTest {
	@Autowired
	DataSource source;

	@Autowired
	AgendaRepository agendaRepository;

	@Test
	void testAgendaRepository() {
		List<Agenda> allAppointments = agendaRepository.findAll();

		assertTrue(allAppointments.size() > 0);
	}
}
