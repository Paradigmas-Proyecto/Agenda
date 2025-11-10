package cr.ac.una.agenda.dto;

import lombok.Data;

/**
 * Representa una tarea planificada con horario asignado
 */

@Data
public class SlotDTO {
    private Long id;
    private String nombre;
    private String inicio;
    private String fin;
}