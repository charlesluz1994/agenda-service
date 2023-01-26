package cluz.com.agenda.api.mapper;

import cluz.com.agenda.api.request.PatientRequest;
import cluz.com.agenda.api.response.PatientResponse;
import cluz.com.agenda.domain.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PatientMapper {

	private final ModelMapper mapper;

	public Patient toPatient(PatientRequest patientRequest){
		return mapper.map(patientRequest, Patient.class);
	}

	public PatientResponse toPatientResponse(Patient patient) {
		return mapper.map(patient, PatientResponse.class);
	}

	public List<PatientResponse> toPatientResponseList(List<Patient> patients){
		return patients.stream()
				.map(this::toPatientResponse)
				.collect(Collectors.toList());
	}

//	public static List<PatientResponse> toPatientResponseList(List<Patient> patients){
//		List<PatientResponse> responses = new ArrayList<>();
//		for(Patient patient:patients){
//			responses.add(toPatientResponse(patient));
//		}
//		return responses;
//	}

//	public static Patient toPatient(PatientRequest patientRequest) {
//		Patient patient = new Patient();
//		patient.setNome(patientRequest.getNome());
//		patient.setSobrenome(patientRequest.getSobrenome());
//		patient.setCpf(patientRequest.getCpf());
//		patient.setEmail(patientRequest.getEmail());
//		return patient;
//	}

//	public static PatientResponse toPatientResponse(Patient patient) {
//		PatientResponse patientResponse = new PatientResponse();
//		patientResponse.setId(patient.getId());
//		patientResponse.setNome(patient.getNome());
//		patientResponse.setSobrenome(patient.getSobrenome());
//		patientResponse.setCpf(patient.getCpf());
//		patientResponse.setEmail(patient.getEmail());
//		return patientResponse;
//	}

//	public static List<PatientResponse> toPatientResponseList(List<Patient> patients){
//		List<PatientResponse> responses = new ArrayList<>();
//		for(Patient patient:patients){
//			responses.add(toPatientResponse(patient));
//		}
//		return responses;
//	}
}
