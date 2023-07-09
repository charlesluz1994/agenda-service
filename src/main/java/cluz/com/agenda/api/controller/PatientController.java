package cluz.com.agenda.api.controller;

import cluz.com.agenda.api.mapper.PatientMapper;
import cluz.com.agenda.api.request.PatientRequest;
import cluz.com.agenda.api.response.PatientResponse;
import cluz.com.agenda.domain.entity.Patient;
import cluz.com.agenda.domain.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper mapper;

    @PostMapping
    public ResponseEntity<PatientResponse> save(@Valid @RequestBody PatientRequest patientRequest) {
        Patient patient = mapper.toPatient(patientRequest);
        Patient patientSaved = patientService.save(patient);
        PatientResponse patientResponse = mapper.toPatientResponse(patientSaved);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientResponse);
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> findAll() {
        List<Patient> patientList = patientService.findAll();
        List<PatientResponse> patientResponses = mapper.toPatientResponseList(patientList);
        return ResponseEntity.status(HttpStatus.OK).body(patientResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@Valid @PathVariable Long id) {
        log.info("Performing search of patient by id: {}", id);
        Optional<Patient> optPatient = patientService.findById(id);

        if (optPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(mapper.toPatientResponse(optPatient.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatientById(@Valid @PathVariable Long id, @RequestBody PatientRequest patientRequest) {
        Patient patient = mapper.toPatient(patientRequest);
        Patient savedPatient = patientService.updatePatient(id, patient);
        PatientResponse patientResponse = mapper.toPatientResponse(savedPatient);
        return ResponseEntity.status(HttpStatus.OK).body(patientResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}