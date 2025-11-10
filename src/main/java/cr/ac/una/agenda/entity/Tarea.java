package cr.ac.una.agenda.entity;



import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa una tarea registrable en el microservicio de Agenda.
 * Esta entidad no modifica la clase Agenda del profesor.
 */
@Entity
@Data
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario propietario de la tarea (si tu sistema es multiusuario). */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    /** Nombre o descripción corta de la tarea. */
    @Column(nullable = false, length = 150)
    private String nombre;

    /** Fecha en la que se desea planificar/realizar la tarea. */
    @Column(nullable = false)
    private LocalDate fecha;

    /** Duración estimada en minutos (> 0). */
    @Column(nullable = false)
    private Integer duracionMinutos;

    /** Hora deseada de inicio (opcional). */
    private LocalTime horaDeseada;

    /** Prioridad de la tarea. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad = Prioridad.MEDIA;

    /** Estado del ciclo de vida de la tarea. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado = Estado.PENDIENTE;

    /** Dependencia simple por ID (opcional). Evita relaciones JPA para mantenerlo simple. */
    @Column(name = "depende_de_id")
    private Long dependeDeId;

    /** Clima requerido/permitido (opcional). */
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Clima climaPermitido;

    /** Observaciones (opcional). */
    @Column(length = 300)
    private String nota;

    // ----- Enums -----
    public enum Prioridad { ALTA, MEDIA, BAJA }
    public enum Estado { PENDIENTE, PLANIFICADA, COMPLETADA }
    public enum Clima { SOLEADO, NUBLADO, LLUVIOSO, VENTOSO }
}
