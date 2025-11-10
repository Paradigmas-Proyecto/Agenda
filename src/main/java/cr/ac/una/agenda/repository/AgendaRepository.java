package cr.ac.una.agenda.repository;

import cr.ac.una.agenda.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository  extends JpaRepository<Agenda, Long> {
}
