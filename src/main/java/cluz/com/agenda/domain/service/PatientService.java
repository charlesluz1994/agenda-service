package cluz.com.agenda.domain.service;

import cluz.com.agenda.config.annotations.Log;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.BusinessException;
import cluz.com.agenda.exception.DataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;

    @Log
    public Patient save(Patient patient) {

        if (patient.getId() != null) {
            throw new IllegalArgumentException("ID must be null for new entities.");
        }

        if (isCPFAlreadyRegistered(patient)) {
            throw new DataIntegrityViolationException("Cpf already exists in the database");
        }
        patient.setDate(ZonedDateTime.now());
        return repository.save(patient);
    }

    public Page<Patient> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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

        return optPatient;
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
