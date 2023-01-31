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
	private final PatientService pacientService;

	public Agenda save(Agenda agenda) {
		Optional<Patient> optPatient = pacientService.findById(agenda.getPaciente().getId());

		if (optPatient.isEmpty()) {
			new BusinessException("Patient not found!");
		}

		Optional<Agenda> optTime = repository.findByHorario(agenda.getHorario());

		if (optTime.isPresent()) {
			new BusinessException("Time already scheduled!");
		}

		agenda.setPaciente(optPatient.get());
		agenda.setDataCriacao(LocalDateTime.now());

		return repository.save(agenda);
	}

	public List<Agenda> findAll() {
		return repository.findAll();
	}

	public Optional<Agenda> findById(Long id) {
		return repository.findById(id);
	}

	public void delete(Agenda agenda) {
		repository.delete(agenda);
	}
}
