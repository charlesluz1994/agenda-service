package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.repository.AgendaRepository;
import cluz.com.agenda.domain.repository.PatientRepository;
import cluz.com.agenda.exception.BusinessException;
import cluz.com.agenda.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendaService {

	/** Fixed length of every appointment slot. */
	private static final Duration APPOINTMENT_DURATION = Duration.ofMinutes(30);

	private final AgendaRepository agendaRepository;
	private final PatientService patientService;
	private final PatientRepository patientRepository;

	@Transactional
	public Agenda save(Agenda agenda) {
		if (agenda.getPatient() == null || agenda.getPatient().getId() == null) {
			throw new NotFoundException("This patient is not registered.");
		}

		var patient = patientService.findPatientById(agenda.getPatient().getId());

		checkAvailability(patient, agenda);

		agenda.setPatient(patient);

		var savedAgenda = agendaRepository.save(agenda);

		patient.getAgendas().add(savedAgenda);
		log.info("Saving new agenda with id: {} for the patient: {}", savedAgenda.getId(), patient.getId());
		return savedAgenda;
	}

	/**
	 * Validate that the new appointment does not overlap any existing appointment of the patient.
	 */
	private void checkAvailability(Patient patient, Agenda newAgenda) {
		var patientAgendas = agendaRepository.findByPatientId(patient.getId());

		boolean overlaps = patientAgendas.stream()
				.anyMatch(existing -> isTimeConflict(existing, newAgenda));

		if (overlaps) {
			throw new BusinessException("Time already scheduled!");
		}
	}

	/**
	 * Two appointments conflict only when their 30-minute slots actually overlap.
	 * Back-to-back appointments (one starting exactly when the other ends) are allowed.
	 */
	private boolean isTimeConflict(Agenda existingAgenda, Agenda newAgenda) {
		LocalDateTime existingStart = existingAgenda.getAppointmentTime();
		LocalDateTime existingEnd = existingStart.plus(APPOINTMENT_DURATION);

		LocalDateTime newStart = newAgenda.getAppointmentTime();
		LocalDateTime newEnd = newStart.plus(APPOINTMENT_DURATION);

		// Overlap requires each slot to start strictly before the other ends,
		// so touching endpoints (back-to-back) do not conflict.
		return newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
	}

	@Transactional(readOnly = true)
	public List<Agenda> findAll() {
		return agendaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Agenda> findAllByPatientId(Long id) {
		var patient = patientRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Patient not found"));
		return agendaRepository.findByPatientId(patient.getId());
	}

	@Cacheable(value = "agendasId")
	@Transactional(readOnly = true)
	public Agenda findById(Long id) {
		return agendaRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Agenda not found"));
	}

	@Transactional
	public void delete(Long id) {
		var agenda = findById(id);

		// Detach from the patient's agenda list to keep the bidirectional relationship consistent.
		var patient = agenda.getPatient();
		if (patient != null) {
			patient.getAgendas().remove(agenda);
			log.info("Deleting agenda with id: {} from the patient: {}", agenda.getId(), patient.getId());
		} else {
			log.info("Deleting agenda with id: {}", agenda.getId());
		}

		agendaRepository.deleteById(agenda.getId());
	}

	@Transactional(readOnly = true)
	public List<Agenda> getTodayAgendas(Long patientId) {
		LocalDate today = LocalDate.now();
		return agendaRepository.findTodayAgendasByPatientId(patientId, today.atStartOfDay(), today.atTime(LocalTime.MAX));
	}
}
