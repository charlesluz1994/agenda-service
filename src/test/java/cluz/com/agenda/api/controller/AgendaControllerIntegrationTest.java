package cluz.com.agenda.api.controller;

import cluz.com.agenda.api.request.AgendaRequest;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.AgendaRepository;
import cluz.com.agenda.domain.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AgendaControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	AgendaRepository agendaRepository;

	private Long patientId;

	@BeforeEach
	void setup() {
		Patient patient = Patient.builder()
				.name("Charles")
				.lastname("Luz")
				.cpf("93922590853")
				.email("charles@gmail.com")
				.build();
		patientId = patientRepository.save(patient).getId();
	}

	@AfterEach
	void tearDown() {
		agendaRepository.deleteAllInBatch();
		patientRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("Given a valid appointment when saving then returns 201 and createdDate is populated by auditing")
	void givenValidAppointment_whenSave_thenReturn201AndAuditsCreatedDate() throws Exception {
		AgendaRequest request = new AgendaRequest("Dentist appointment", LocalDateTime.now().plusDays(1).withNano(0), patientId);

		mockMvc.perform(post("/agenda")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.patientId").value(patientId))
				.andExpect(jsonPath("$.createdDate").isNotEmpty())
				.andDo(print());
	}

	@Test
	@DisplayName("Given an unknown patient when saving an appointment then returns 404")
	void givenUnknownPatient_whenSave_thenReturn404() throws Exception {
		AgendaRequest request = new AgendaRequest("Dentist appointment", LocalDateTime.now().plusDays(1).withNano(0), 9999L);

		mockMvc.perform(post("/agenda")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andDo(print());
	}
}
