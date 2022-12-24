package cluz.com.agenda.domain.service;

import cluz.com.agenda.domain.entity.Paciente;
import cluz.com.agenda.domain.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    public Paciente save(Paciente paciente){
        
        return repository.save(paciente);
    }

    public List<Paciente> findAll(){
        return repository.findAll();
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    public Optional<Paciente> findById(Long id){
        return repository.findById(id);
    }

//    public Paciente updatePaciente(){
//
//    }
}
