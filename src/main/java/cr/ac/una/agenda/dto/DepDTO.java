package cr.ac.una.agenda.dto;

import lombok.Data;

/**
 * Representa una dependencia entre tareas
 */

@Data
public class DepDTO {
    private Long tarea;      // ID de la tarea que tiene dependencia
    private Long dependeDe;  // ID de la tarea de la que depende
}