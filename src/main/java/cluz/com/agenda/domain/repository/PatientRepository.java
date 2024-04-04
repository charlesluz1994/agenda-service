package cluz.com.agenda.domain.repository;

import cluz.com.agenda.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
@Query("SELECT obj from Patient obj WHERE obj.cpf =:cpf")
    Optional<Patient> findByCpf(@Param("cpf") String cpf);
}
