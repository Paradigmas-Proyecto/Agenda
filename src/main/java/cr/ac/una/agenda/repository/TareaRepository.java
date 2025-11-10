package cr.ac.una.agenda.repository;

import cr.ac.una.agenda.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad Tarea.
 * Permite operaciones CRUD automáticas y consultas personalizadas.
 */
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    /**
     * Obtiene todas las tareas de un usuario para una fecha específica.
     */
    List<Tarea> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    /**
     * Lista todas las tareas por estado.
     */
    List<Tarea> findByEstado(Tarea.Estado estado);

    /**
     * Lista todas las tareas por prioridad.
     */
    List<Tarea> findByPrioridad(Tarea.Prioridad prioridad);

    /**
     * Lista todas las tareas que dependen de otra tarea específica.
     */
    List<Tarea> findByDependeDeId(Long dependeDeId);
}
