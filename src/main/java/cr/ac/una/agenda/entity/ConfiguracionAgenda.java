package cr.ac.una.agenda.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

/**
 * Configuración general del día para la planificación en la Agenda.
 * Permite definir la hora de inicio y el tiempo disponible diario
 * que se usará al generar el plan en Prolog.
 */
@Entity
@Data
@Table(name = "configuracion_agenda")
public class ConfiguracionAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario (si cada usuario tiene su configuración). */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    /** Hora en la que inicia la jornada diaria (por ejemplo, 08:00). */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio = LocalTime.of(8, 0);

    /** Cantidad total de minutos disponibles en el día. */
    @Column(name = "minutos_disponibles", nullable = false)
    private Integer minutosDisponibles = 480; // 8 horas por defecto

    /** Observación o nota opcional. */
    @Column(length = 300)
    private String nota;
}