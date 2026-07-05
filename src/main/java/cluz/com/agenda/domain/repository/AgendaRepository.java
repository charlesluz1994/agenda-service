package cluz.com.agenda.domain.repository;

import cluz.com.agenda.domain.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

	List<Agenda> findByPatientId(Long patientId);

	// Appointments of a given day for a patient.
	@Query("SELECT a FROM Agenda a " +
			"WHERE a.patient.id = :patientId " +
			"AND a.appointmentTime BETWEEN :startOfDay AND :endOfDay")
	List<Agenda> findTodayAgendasByPatientId(
			@Param("patientId") Long patientId,
			@Param("startOfDay") LocalDateTime startOfDay,
			@Param("endOfDay") LocalDateTime endOfDay
	);
}