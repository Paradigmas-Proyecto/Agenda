package cr.ac.una.agenda.service;

// Importa la entidad y el repositorio asociados a la configuración de agenda.
import cr.ac.una.agenda.entity.ConfiguracionAgenda;
import cr.ac.una.agenda.repository.ConfiguracionAgendaRepository;

// Anotación que marca esta clase como un "Service" de Spring (componente de lógica de negocio).
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que gestiona la lógica de negocio para la configuración de la agenda.
 * Incluye validaciones, operaciones CRUD y consultas por usuario.
 */
@Service
public class ConfiguracionAgendaService {

    // Inyección del repositorio para acceder a la base de datos.
    private final ConfiguracionAgendaRepository repo;

    // Constructor que inyecta automáticamente el repositorio cuando Spring crea el bean.
    public ConfiguracionAgendaService(ConfiguracionAgendaRepository repo) {
        this.repo = repo;
    }

    /* =======================
       VALIDACIONES BÁSICAS
       ======================= */
    /**
     * Método privado que valida los datos antes de guardar o actualizar.
     * Lanza una excepción si falta algún campo requerido o los valores son inválidos.
     */
    private void validar(ConfiguracionAgenda c) {
        if (c.getUsuarioId() == null)
            throw new IllegalArgumentException("El campo usuarioId es obligatorio.");
        if (c.getHoraInicio() == null)
            throw new IllegalArgumentException("Debe indicar la hora de inicio del día.");
        if (c.getMinutosDisponibles() == null || c.getMinutosDisponibles() <= 0)
            throw new IllegalArgumentException("Los minutos disponibles deben ser mayores a 0.");
    }

    /* =======================
       CRUD COMPLETO
       ======================= */

    /**
     * Devuelve todas las configuraciones almacenadas en la base de datos.
     */
    public List<ConfiguracionAgenda> listar() {
        return repo.findAll();
    }

    /**
     * Busca una configuración específica por su ID.
     * Si no existe, lanza una excepción con un mensaje descriptivo.
     */
    public ConfiguracionAgenda obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada con ID " + id));
    }

    /**
     * Crea una nueva configuración para un usuario.
     * Valida los datos y evita que un mismo usuario tenga más de una configuración.
     */
    public ConfiguracionAgenda crear(ConfiguracionAgenda c) {
        validar(c);
        // Verifica si el usuario ya tiene una configuración registrada
        repo.findByUsuarioId(c.getUsuarioId()).ifPresent(conf -> {
            throw new IllegalArgumentException("El usuario ya tiene una configuración registrada.");
        });
        // Se asegura de que el ID sea nulo (nuevo registro)
        c.setId(null);
        // Guarda en la base de datos
        return repo.save(c);
    }

    /**
     * Actualiza una configuración existente.
     * Primero valida los datos, luego busca la existente y reemplaza los valores editables.
     */
    public ConfiguracionAgenda actualizar(Long id, ConfiguracionAgenda c) {
        validar(c);
        // Busca la configuración actual o lanza error si no existe
        ConfiguracionAgenda actual = obtener(id);
        // Solo actualiza los campos editables
        actual.setHoraInicio(c.getHoraInicio());
        actual.setMinutosDisponibles(c.getMinutosDisponibles());
        actual.setNota(c.getNota());
        // Guarda los cambios
        return repo.save(actual);
    }

    /**
     * Elimina una configuración según su ID.
     */
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /* =======================
       CONSULTA POR USUARIO
       ======================= */

    /**
     * Devuelve la configuración de un usuario específico.
     * Si no se encuentra, lanza una excepción indicando el problema.
     */
    public ConfiguracionAgenda obtenerPorUsuario(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe configuración para el usuario con ID " + usuarioId));
    }
}

