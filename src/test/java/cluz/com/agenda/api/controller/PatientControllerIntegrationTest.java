package cluz.com.agenda.api.controller;

import cluz.com.agenda.api.request.PatientRequest;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PatientControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PatientRepository repository;

    @BeforeEach
    void setup() {
        Patient patient = new Patient();
        patient.setName("Charles");
        patient.setLastname("Luz");
        patient.setCpf("00100200388");
        patient.setEmail("charles@gmail.com");
        repository.save(patient);
    }

    @AfterEach
    void down() {
        repository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Given a patient when save patient then return a 200 status code")
    void given_Patient_when_SavePatient_then_Return201StatusCode() throws Exception {
        PatientRequest patient = PatientRequest.builder()
                .name("Charles")
                .lastname("Luz")
                .cpf("00100200300")
                .email("charles@gmail.com").build();

        mockMvc.perform(post("/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("given a request when find all patients then return 200 status code")
    void given_ID_when_FindAllPatients_then_Return200StatusCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/patient"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Given an ID when find patient ByID then return a 200 status code")
    void given_ID_when_FindPatientByID_then_Return200StatusCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/patient/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Given an ID when update patient request then return 200 status code")
    void given_ID_when_UpdatePatient_then_Return200StatusCode() throws Exception {
        PatientRequest patient = PatientRequest.builder()
                .name("Charlie")
                .lastname("Santos")
                .cpf("418292378")
                .email("charles@gmail.com").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/patient/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("Given an ID when delete patient then return 204 status code")
    void given_ID_when_DeletePatient_then_Return204StatusCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/patient/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());

    }

    @Test
    @DisplayName("Given a patient when save with patient already existence then throw an exception")
    void given_Patient_when_SavePatientWithExistingCPF_then_ThrowAnException() throws Exception {
        PatientRequest patient = PatientRequest.builder()
                .name("Charles")
                .lastname("Luz")
                .cpf("00100200388")
                .email("charles@gmail.com").build();

        mockMvc.perform(post("/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BusinessException))
                .andDo(print());
    }
}