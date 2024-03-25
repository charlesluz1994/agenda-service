package cluz.com.agenda.api.controller;

import cluz.com.agenda.api.mapper.AgendaMapper;
import cluz.com.agenda.api.request.AgendaRequest;
import cluz.com.agenda.api.response.AgendaResponse;
import cluz.com.agenda.domain.entity.Agenda;
import cluz.com.agenda.domain.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {
    private final AgendaService agendaService;
    private final AgendaMapper mapper;

    @PostMapping
    public ResponseEntity<AgendaResponse> save(@Valid @RequestBody AgendaRequest agendaRequest) {
        var agenda = mapper.toAgenda(agendaRequest);
        var savedAgenda = agendaService.save(agenda);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAgendaResponse(savedAgenda));
    }

    @GetMapping
    public ResponseEntity<List<AgendaResponse>> findAll() {
        List<Agenda> listAgenda = agendaService.findAll();
        List<AgendaResponse> agendaResponses = mapper.toAgendaResponseList(listAgenda);
        return ResponseEntity.status(HttpStatus.OK).body(agendaResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> getAgendaById(@Valid @PathVariable Long id) {
        var optAgenda = agendaService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toAgendaResponse(optAgenda.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgendaById(@PathVariable Long id) {
        agendaService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //TODO: Update agenda.

}
