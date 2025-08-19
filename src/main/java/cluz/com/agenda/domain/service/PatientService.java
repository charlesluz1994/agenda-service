package cluz.com.agenda.domain.service;

import cluz.com.agenda.config.annotations.Log;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.DataIntegrityViolationException;
import cluz.com.agenda.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PatientService {
	private final PatientRepository repository;

	@Log
	public Patient save(Patient patient) {
		if (isCPFAlreadyRegistered(patient.getCpf())) {
			throw new DataIntegrityViolationException("Cpf is already registered.");
		}
		patient.setCreatedDate(ZonedDateTime.now());
		return repository.save(patient);
	}

	public Page<Patient> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public void delete(Long id) {
		var patient = findPatientById(id);
		log.info("Deleting patient by id: {}", id);

		repository.deleteById(patient.getId());

	}

	public Patient findPatientById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Patient not registered!"));
	}

	public Patient updatePatient(Long id, Patient patient) {
		var existentPatient = findPatientById(id);

		if (!existentPatient.getCpf().equals(patient.getCpf()) && isCPFAlreadyRegistered(patient.getCpf())) {
			throw new DataIntegrityViolationException("Cpf is already registered.");
		}

		log.info("Updating patient with id: {}", id);

		existentPatient.setName(patient.getName());
		existentPatient.setLastname(patient.getLastname());
		existentPatient.setCpf(patient.getCpf());
		existentPatient.setEmail(patient.getEmail());
		return repository.save(existentPatient);
	}

	private boolean isCPFAlreadyRegistered(String cpfPatient) {
		var optionalPatient = repository.findByCpf(cpfPatient);
		return optionalPatient.isPresent();
	}
}
