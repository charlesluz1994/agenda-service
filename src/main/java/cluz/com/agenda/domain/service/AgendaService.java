package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.AgendaRepository;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.BusinessException;
import cluz.com.agenda.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaService {
	private final AgendaRepository agendaRepository;
	private final PatientService patientService;
	private final PatientRepository patientRepository;

	public Agenda save(Agenda agenda) {
		if (agenda.getPatient().getId() == null) {
			throw new NotFoundException("This patient is not registered.!");
		}

		var patient = patientService.findPatientById(agenda.getPatient().getId());

		checkAvailability(patient, agenda);

		agenda.setPatient(patient);
		agenda.setCreatedDate(LocalDateTime.now());

		var savedAgenda = agendaRepository.save(agenda);

		patient.getAgendas().add(savedAgenda);

		return savedAgenda;
	}

	/**
	 * Validate if there is conflict of appointments time before save.
	 *
	 * @param patient
	 * @param newAgenda
	 */
	private void checkAvailability(Patient patient, Agenda newAgenda) {
		var patientAgendas = agendaRepository.findByPatientId(patient.getId());

		for (Agenda existingAgenda : patientAgendas) {
			if (isTimeConflict(existingAgenda, newAgenda)) {
				throw new BusinessException("Time already scheduled!");
			}
		}
	}

	/**
	 * Verify if the new agenda is 30 min before or 30 min after the existing agenda.Override of agenda is not possible.
	 *
	 * @param existingAgenda
	 * @param newAgenda
	 * @return
	 */
	private boolean isTimeConflict(Agenda existingAgenda, Agenda newAgenda) {
		LocalDateTime existingStart = existingAgenda.getAppointmentTime();
		LocalDateTime existingEnd = existingStart.plusMinutes(30);

		LocalDateTime newStart = newAgenda.getAppointmentTime();
		LocalDateTime newEnd = newStart.plusMinutes(30);

		return !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
		//return newEnd.isBefore(existingStart.minusMinutes(30)) && newStart.isAfter(existingEnd.plusMinutes(30));
	}


	public List<Agenda> findAll() {
		return agendaRepository.findAll();
	}

	public List<Agenda> findAllByPatientId(Long id) {
		var patient = patientRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Patient not found"));
		return agendaRepository.findByPatientId(patient.getId());
	}

	@Cacheable(value = "agendasId")
	public Agenda findById(Long id) {
		return agendaRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Agenda not found"));
	}

	public void delete(Long id) {
		var agenda = findById(id);

		// Remove from patient's agenda list to maintain bidirectional relationship
		var patient = agenda.getPatient();
		if (patient != null) {
			patient.getAgendas().remove(agenda);
		}

		agendaRepository.deleteById(agenda.getId());
	}

	public List<Agenda> getTodayAgendas(Long patientId) {
		LocalDate today = LocalDate.now();
		return agendaRepository.findTodayAgendasByPatientId(patientId, today, today);
	}
}
