package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.AgendaRepository;
import cluz.com.agenda.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        Agenda expectedAgenda = buildAgenda();
        when(patientService.findById(expectedAgenda.getId())).thenReturn(Optional.of(expectedAgenda.getPatient()));
        when(repository.findByAppointmentTime(expectedAgenda.getAppointmentTime())).thenReturn(Optional.empty());

        // When
        agendaService.save(expectedAgenda);

        // Then
        verify(patientService).findById(expectedAgenda.getId());
        verify(repository).findByAppointmentTime(expectedAgenda.getAppointmentTime());

        // Get the agenda at the moment exactly that is being saved.
        verify(repository).save(agendaCaptor.capture());
        Agenda savedAgenda = agendaCaptor.getValue();

        assertEquals(expectedAgenda, savedAgenda, "agenda should match");
        assertEquals(expectedAgenda.getPatient(), savedAgenda.getPatient(), "patient should match");
        assertNotNull(savedAgenda.getCreatedDate(), "Create date of agenda cannot be null");
        assertNotNull(savedAgenda.getPatient());

    }

    @Test
    @DisplayName("Given a Agenda when Patient is not valid then Throw BusinessException")
    void givenAgenda_whenPatientIsNotValid_thenThrowBusinessException() {
        Agenda expectedAgenda = Agenda.builder()
                .id(1L)
                .description("Charles Appointment - Dentist")
                .appointmentTime(LocalDateTime.now())
                .createdDate(LocalDateTime.now()).build();

        // when(patientService.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        // When
        // Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            agendaService.save(expectedAgenda);
        });

        assertEquals("Patient not found!", exception.getMessage(), "exception message should match");
    }

    @Test
    @DisplayName("Given a Agenda when time already scheduled then Throw BusinessException")
    void givenAgenda_whenTimeAlreadyScheduled_thenThrowBusinessException() {
        Agenda expectedAgenda = buildAgenda();
        when(patientService.findById(expectedAgenda.getId())).thenReturn(Optional.of(expectedAgenda.getPatient()));
        when(repository.findByAppointmentTime(expectedAgenda.getAppointmentTime())).thenReturn(Optional.of(expectedAgenda));

        // When
        // Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            agendaService.save(expectedAgenda);
        });

        assertEquals("Time already scheduled!", exception.getMessage(), "exception message should match");
    }

    private Agenda buildAgenda() {
        return Agenda.builder()
                .id(1L)
                .patient(buildPatient())
                .description("Charles Appointment - Dentist")
                .appointmentTime(LocalDateTime.now())
                .createdDate(LocalDateTime.now()).build();
    }

    private Patient buildPatient() {
        return Patient.builder()
                .id(1L)
                .name("Charles")
                .cpf("428282000111").build();
    }
}