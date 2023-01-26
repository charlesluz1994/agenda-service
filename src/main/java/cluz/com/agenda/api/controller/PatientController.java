package cluz.com.agenda.api.controller;

import cluz.com.agenda.api.mapper.PatientMapper;
import cluz.com.agenda.api.request.PatientRequest;
import cluz.com.agenda.api.response.PatientResponse;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("patient")
@AllArgsConstructor
public class PatientController {

	private final PatientService patientService;
	private final PatientMapper mapper;

	@PostMapping
	public ResponseEntity<PatientResponse> save(@RequestBody PatientRequest request){
		Patient patient = mapper.toPatient(request);
		Patient patientSaved = patientService.save(patient);
		PatientResponse patientResponse = mapper.toPatientResponse(patientSaved);
		return ResponseEntity.status(HttpStatus.CREATED).body(patientResponse);
	}

	@GetMapping
	public ResponseEntity<List<PatientResponse>> findAll(){
		List<Patient> patientList = patientService.findAll();
		List<PatientResponse> patientResponses = mapper.toPatientResponseList(patientList);
		return ResponseEntity.status(HttpStatus.OK).body(patientResponses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id){
		Optional<Patient> optPatient = patientService.findById(id);

		if(optPatient.isEmpty()){
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(mapper.toPatientResponse(optPatient.get()));
	}

	@PutMapping
	public ResponseEntity<PatientResponse> updatePatientById(@RequestBody Patient patient){
		Patient savedPatient = patientService.save(patient);
		PatientResponse patientResponse = mapper.toPatientResponse(savedPatient);
		return ResponseEntity.status(HttpStatus.OK).body(patientResponse);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePatient(@PathVariable Long id){
		patientService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}