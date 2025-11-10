package cr.ac.una.agenda.repository;

import cr.ac.una.agenda.entity.ConfiguracionAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad ConfiguracionAgenda.
 * Permite operaciones CRUD y búsqueda por usuario.
 */
public interface ConfiguracionAgendaRepository extends JpaRepository<ConfiguracionAgenda, Long> {
    /**
     * Busca la configuración de un usuario específico (si existe).
     */
    Optional<ConfiguracionAgenda> findByUsuarioId(Long usuarioId);
}
