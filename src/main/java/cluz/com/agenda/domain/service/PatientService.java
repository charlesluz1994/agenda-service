package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {
	@Autowired
	private PatientRepository repository;

	public Patient save(Patient patient) {

		boolean existeCpf = false;

		Optional<Patient> optPatient = repository.findByCpf(patient.getCpf());

		if(optPatient.isPresent()){
			if(!optPatient.get().getId().equals(patient.getId())){
				existeCpf = true;
			}
		}

		if(existeCpf){
			throw new BusinessException("Cpf already exists.");
		}
		
		return repository.save(patient);
	}

	public List<Patient> findAll() {
		return repository.findAll();
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Optional<Patient> findById(Long id) {
		return repository.findById(id);
	}

//    public Paciente updatePaciente(){
//
//    }
}
