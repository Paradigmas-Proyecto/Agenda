package cr.ac.una.agenda.dto;

import lombok.Data;
import java.util.List;

/**
 * Tarea en formato que comprende PrologAPI
 */

@Data
public class TaskDTO {
    private Long id;
    private String nombre;
    private int prioridad;
    private int dur;
    private List<String> climas;
}
