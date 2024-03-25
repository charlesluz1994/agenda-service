package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.AgendaRepository;
import cluz.com.agenda.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaService {
    private final AgendaRepository repository;
    private final PatientService patientService;

    public Agenda save(Agenda agenda) {
        if (agenda.getPatient() == null) {
            throw new BusinessException("Patient not found!");
        }

        Optional<Patient> optPatient = patientService.findById(agenda.getPatient().getId());

        Optional<Agenda> optTime = repository.findByAppointmentTime(agenda.getAppointmentTime());

        if (optTime.isPresent()) {
            throw new BusinessException("Time already scheduled!");
        }

        agenda.setPatient(optPatient.get());
        agenda.setCreatedDate(LocalDateTime.now());

        return repository.save(agenda);
    }

    public List<Agenda> findAll() {
        return repository.findAll();
    }

    public Optional<Agenda> findById(Long id) {
        var optAgenda = repository.findById(id);

        if (optAgenda.isEmpty()) {
            throw new BusinessException("Agenda not found!");
        }
        return optAgenda;
    }

    public void delete(Long id) {
        var optAgenda = repository.findById(id);

        if (optAgenda.isEmpty()) {
            throw new BusinessException("Agenda not found!");
        }
        repository.deleteById(id);
    }
}
