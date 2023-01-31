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
}
