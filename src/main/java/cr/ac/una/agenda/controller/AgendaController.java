package cr.ac.una.agenda.controller;

import cr.ac.una.agenda.dto.PlanRequest;
import cr.ac.una.agenda.dto.PlanResponse;
import cr.ac.una.agenda.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AgendaController {

    @Autowired
    AgendaService  agendaService;

    @GetMapping("/prolog")
    public Integer prolog(){
        return agendaService.sum(1,19);
    }

    @PostMapping("/agenda/planificar")
    public PlanResponse planificar(@RequestBody PlanRequest request) {
        return agendaService.generarPlan(request);
    }

    @PostMapping("/agenda/replanificar")
    public PlanResponse replanificar(@RequestBody PlanRequest request) {
        return agendaService.replanificar(request);
    }
}
