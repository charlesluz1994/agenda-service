package cluz.com.agenda.api.controller;

import cluz.com.agenda.api.mapper.PatientMapper;
import cluz.com.agenda.api.request.PatientRequest;
import cluz.com.agenda.api.response.PatientResponse;
import cluz.com.agenda.domain.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

@Slf4j
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;
	private final PatientMapper mapper;

	@PostMapping
	public ResponseEntity<PatientResponse> save(@Valid @RequestBody PatientRequest patientRequest) {
		var savedPatient = patientService.save(mapper.toPatient(patientRequest));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPatientResponse(savedPatient));
	}

	@GetMapping
	public ResponseEntity<List<PatientResponse>> findAll(Pageable pageable) {
		var patientResponses = patientService.findAll(pageable)
				.stream()
				.map(mapper::toPatientResponse)
				.toList();

		return ResponseEntity.status(HttpStatus.OK).body(patientResponses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PatientResponse> getPatientById(@Valid @PathVariable Long id) {
		log.info("Finding patient by id: {}", id);
		var patient = patientService.findPatientById(id);

		return ResponseEntity.status(HttpStatus.OK).body(mapper.toPatientResponse(patient));
	}

	@PutMapping("/{id}")
	public ResponseEntity<PatientResponse> updatePatientById(@Valid @PathVariable Long id, @RequestBody PatientRequest patientRequest) {
		log.info("Updating patient by id: {}", id);
		var updatedPatient = patientService.updatePatient(id, mapper.toPatient(patientRequest));
		return ResponseEntity.status(HttpStatus.OK).body(mapper.toPatientResponse(updatedPatient));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
		patientService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}