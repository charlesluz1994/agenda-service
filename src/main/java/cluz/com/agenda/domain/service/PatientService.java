package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.BusinessException;
import cluz.com.agenda.exception.DataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;

    public Patient save(Patient patient) {

        if (isCPFAlreadyRegistered(patient)) {
            throw new DataIntegrityViolationException("Cpf already exists in the database");
        }
        return repository.save(patient);
    }

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
        log.info("Performing search of patient by id: {}", id);
    }

    public Optional<Patient> findById(Long id) {
        var optPatient = repository.findById(id);

        if (optPatient.isEmpty()) {
            throw new BusinessException("Patient not registered!");
        }

        return repository.findById(id);
    }

    public Patient updatePatient(Long id, Patient patient) {
        var optPatient = this.findById(id);
        if (optPatient.isEmpty()) {
            throw new BusinessException("Patient not registered!");
        }
        patient.setId(id);
        return save(patient);
    }

    private boolean isCPFAlreadyRegistered(Patient objPatient) {
        var optionalPatient = repository.findByCpf(objPatient.getCpf());
        return optionalPatient.isPresent();
    }
}
