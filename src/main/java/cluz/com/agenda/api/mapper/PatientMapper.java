package cluz.com.agenda.api.mapper;

import cluz.com.agenda.api.request.PatientRequest;
import cluz.com.agenda.api.response.PatientResponse;
import cluz.com.agenda.domain.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PatientMapper {

	PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

	PatientResponse toPatientResponse(Patient patient);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "agendas", ignore = true)
	Patient toPatient(PatientRequest patientRequest);
}