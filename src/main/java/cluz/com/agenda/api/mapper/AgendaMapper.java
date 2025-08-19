package cluz.com.agenda.api.mapper;

import cluz.com.agenda.api.request.AgendaRequest;
import cluz.com.agenda.api.response.AgendaResponse;
import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface AgendaMapper {

	AgendaMapper INSTANCE = Mappers.getMapper(AgendaMapper.class);

	@Mapping(target = "patientId", source = "patient.id")
	@Mapping(target = "patientName", source = "patient.name")
	@Mapping(target = "patientEmail", source = "patient.email")
	AgendaResponse toAgendaResponse(Agenda agenda);

	List<AgendaResponse> toAgendaResponseList(List<Agenda> agendas);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "patient", source = "patientId")
	Agenda toAgenda(AgendaRequest agendaRequest);

	default Patient mapPatientId(Long patientId) {
		if (patientId == null) {
			return null;
		}
		Patient patient = new Patient();
		patient.setId(patientId);
		return patient;
	}
}
