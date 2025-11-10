package cr.ac.una.agenda.controller;

import cr.ac.una.agenda.entity.Tarea;
import cr.ac.una.agenda.service.TareaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    /* ===== CRUD básico ===== */

    @GetMapping
    public List<Tarea> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Tarea obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public Tarea crear(@RequestBody Tarea tarea) {
        return service.crear(tarea);
    }

    @PutMapping("/{id}")
    public Tarea actualizar(@PathVariable Long id, @RequestBody Tarea tarea) {
        return service.actualizar(id, tarea);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    /* ===== Consultas útiles ===== */

    // /api/tareas/por-fecha?usuarioId=99&fecha=2025-10-26
    @GetMapping("/por-fecha")
    public List<Tarea> listarPorUsuarioYFecha(
            @RequestParam Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.listarPorUsuarioYFecha(usuarioId, fecha);
    }

    // /api/tareas/por-estado?estado=PENDIENTE
    @GetMapping("/por-estado")
    public List<Tarea> listarPorEstado(@RequestParam Tarea.Estado estado) {
        return service.listarPorEstado(estado);
    }

    // /api/tareas/por-prioridad?prioridad=ALTA
    @GetMapping("/por-prioridad")
    public List<Tarea> listarPorPrioridad(@RequestParam Tarea.Prioridad prioridad) {
        return service.listarPorPrioridad(prioridad);
    }

    /* ===== Helpers ===== */

    // Cambiar estado rápido: /api/tareas/5/estado?nuevo=COMPLETADA
    @PatchMapping("/{id}/estado")
    public Tarea cambiarEstado(@PathVariable Long id, @RequestParam Tarea.Estado nuevo) {
        return service.cambiarEstado(id, nuevo);
    }
}
