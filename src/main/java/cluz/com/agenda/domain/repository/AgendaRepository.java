package cluz.com.agenda.domain.repository;

import cluz.com.agenda.domain.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
	Optional<Agenda> findByAppointmentTime(LocalDateTime appointmentTime);

	List<Agenda> findByPatientId(Long patientId);

	@Query("SELECT a FROM Agenda a WHERE a.patient.id = :patientId ORDER BY a.appointmentTime")
	List<Agenda> findByPatientIdOrderByAppointmentTime(@Param("patientId") Long patientId);

	@Query("SELECT a FROM Agenda a WHERE a.patient.id = :patientId AND a.appointmentTime > :now ORDER BY a.appointmentTime")
	List<Agenda> findFutureAgendasByPatientId(@Param("patientId") Long patientId,
											  @Param("now") LocalDateTime now);

	boolean existsByPatientIdAndAppointmentTime(Long patientId, LocalDateTime appointmentTime);

	// Agendas of today for patient
	@Query("SELECT a FROM Agenda a " +
			"WHERE a.patient.id = :patientId " +
			"AND a.appointmentTime BETWEEN :startOfDay AND :endOfDay")
	List<Agenda> findTodayAgendasByPatientId(
			@Param("patientId") Long patientId,
			@Param("startOfDay") LocalDate startOfDay,
			@Param("endOfDay") LocalDate endOfDay
	);
}