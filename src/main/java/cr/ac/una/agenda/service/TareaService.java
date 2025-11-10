package cr.ac.una.agenda.service;

import cr.ac.una.agenda.entity.Tarea;
import cr.ac.una.agenda.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository repo;

    public TareaService(TareaRepository repo) {
        this.repo = repo;
    }

    /* =======================
       VALIDACIONES BÁSICAS
       ======================= */
    private void validar(Tarea t) {
        if (t.getUsuarioId() == null) throw new IllegalArgumentException("usuarioId es obligatorio");
        if (!StringUtils.hasText(t.getNombre())) throw new IllegalArgumentException("nombre es obligatorio");
        if (t.getNombre().length() > 150) throw new IllegalArgumentException("nombre demasiado largo (max 150)");
        if (t.getFecha() == null) throw new IllegalArgumentException("fecha es obligatoria (yyyy-MM-dd)");
        if (t.getDuracionMinutos() == null || t.getDuracionMinutos() <= 0)
            throw new IllegalArgumentException("duracionMinutos debe ser > 0");
        if (t.getPrioridad() == null) throw new IllegalArgumentException("prioridad es obligatoria");
        if (t.getEstado() == null) throw new IllegalArgumentException("estado es obligatorio");
        // Evitar auto-dependencia
        if (t.getDependeDeId() != null && t.getId() != null && t.getDependeDeId().equals(t.getId()))
            throw new IllegalArgumentException("Una tarea no puede depender de sí misma");
        // Validar dependencias circulares
        if (t.getDependeDeId() != null) {
            validarDependenciaCircular(t);
        }
    }

    /**
     * Valida que no existan dependencias circulares
     */
    private void validarDependenciaCircular(Tarea tarea) {
        java.util.Set<Long> visitados = new java.util.HashSet<>();
        Long actual = tarea.getDependeDeId();
        
        while (actual != null && !visitados.contains(actual)) {
            if (actual.equals(tarea.getId())) {
                throw new IllegalArgumentException("Dependencia circular detectada: la tarea eventualmente dependería de sí misma");
            }
            visitados.add(actual);
            
            Tarea padre = repo.findById(actual).orElse(null);
            actual = padre != null ? padre.getDependeDeId() : null;
        }
    }

    /* =======================
       CRUD
       ======================= */
    public List<Tarea> listar() {
        return repo.findAll();
    }

    public Tarea obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
    }

    public Tarea crear(Tarea t) {
        validar(t);
        // En creación, id debe ser null
        t.setId(null);
        return repo.save(t);
    }

    public Tarea actualizar(Long id, Tarea t) {
        validar(t);
        Tarea actual = obtener(id);

        // Campos editables
        actual.setUsuarioId(t.getUsuarioId());
        actual.setNombre(t.getNombre());
        actual.setFecha(t.getFecha());
        actual.setDuracionMinutos(t.getDuracionMinutos());
        actual.setHoraDeseada(t.getHoraDeseada());
        actual.setPrioridad(t.getPrioridad());
        actual.setEstado(t.getEstado());
        actual.setDependeDeId(t.getDependeDeId());
        actual.setClimaPermitido(t.getClimaPermitido());
        actual.setNota(t.getNota());

        return repo.save(actual);
    }

    public void eliminar(Long id) {
        // Eliminar recursivamente todas las tareas que dependen de esta
        List<Tarea> hijas = repo.findByDependeDeId(id);
        for (Tarea hija : hijas) {
            eliminar(hija.getId());
        }
        repo.deleteById(id);
    }

    /* =======================
       CONSULTAS ÚTILES
       ======================= */
    public List<Tarea> listarPorUsuarioYFecha(Long usuarioId, LocalDate fecha) {
        return repo.findByUsuarioIdAndFecha(usuarioId, fecha);
    }

    public List<Tarea> listarPorEstado(Tarea.Estado estado) {
        return repo.findByEstado(estado);
    }

    public List<Tarea> listarPorPrioridad(Tarea.Prioridad prioridad) {
        return repo.findByPrioridad(prioridad);
    }

    /* =======================
       AYUDAS
       ======================= */
    public Tarea cambiarEstado(Long id, Tarea.Estado nuevo) {
        Tarea t = obtener(id);
        t.setEstado(nuevo);
        return repo.save(t);
    }
}
