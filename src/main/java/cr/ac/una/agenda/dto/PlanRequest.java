package cr.ac.una.agenda.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * Request que recibe el endpoint /api/agenda/planificar
 */

@Data
public class PlanRequest {
    private Long usuarioId;
    private LocalDate fecha;
    private String climaDia;
    private Integer minutosDisponibles;
    private String horaInicio;

    private List<TaskDTO> tasks;
    private List<DepDTO> deps;
}