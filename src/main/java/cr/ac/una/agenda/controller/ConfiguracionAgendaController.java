package cr.ac.una.agenda.controller;

import cr.ac.una.agenda.entity.ConfiguracionAgenda;
import cr.ac.una.agenda.service.ConfiguracionAgendaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la configuración de la agenda.
 * Permite crear, listar, editar, eliminar y consultar configuraciones por usuario.
 */
@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionAgendaController {

    private final ConfiguracionAgendaService service;

    public ConfiguracionAgendaController(ConfiguracionAgendaService service) {
        this.service = service;
    }

    /* =======================
       CRUD BÁSICO
       ======================= */

    /** Obtiene todas las configuraciones registradas. */
    @GetMapping
    public List<ConfiguracionAgenda> listar() {
        return service.listar();
    }

    /** Obtiene una configuración por su ID. */
    @GetMapping("/{id}")
    public ConfiguracionAgenda obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    /** Crea una nueva configuración. */
    @PostMapping
    public ConfiguracionAgenda crear(@RequestBody ConfiguracionAgenda configuracion) {
        return service.crear(configuracion);
    }

    /** Actualiza una configuración existente. */
    @PutMapping("/{id}")
    public ConfiguracionAgenda actualizar(@PathVariable Long id,
                                          @RequestBody ConfiguracionAgenda configuracion) {
        return service.actualizar(id, configuracion);
    }

    /** Elimina una configuración por su ID. */
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    /* =======================
       CONSULTA POR USUARIO
       ======================= */

    /** Busca la configuración asociada a un usuario específico. */
    @GetMapping("/por-usuario")
    public ConfiguracionAgenda obtenerPorUsuario(@RequestParam Long usuarioId) {
        return service.obtenerPorUsuario(usuarioId);
    }
}