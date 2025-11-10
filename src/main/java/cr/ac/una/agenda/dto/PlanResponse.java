package cr.ac.una.agenda.dto;

import lombok.Data;
import java.util.List;

/**
 * Response que devuelve PrologAPI y que reenvía al Front
 */

@Data
public class PlanResponse {
    private boolean posible;
    private int minutosDisponibles;
    private int minutosUsados;
    private int minutosSobrantes;
    private List<SlotDTO> tareasPlan;
    private List<TaskDTO> noProgramadas;
    private String sugerencias; // Sugerencias cuando el plan no es posible
}


