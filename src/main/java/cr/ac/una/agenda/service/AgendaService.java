package cr.ac.una.agenda.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import cr.ac.una.agenda.dto.*;
import cr.ac.una.agenda.entity.ConfiguracionAgenda;
import cr.ac.una.agenda.entity.Tarea;
import cr.ac.una.agenda.repository.ConfiguracionAgendaRepository;
import cr.ac.una.agenda.repository.TareaRepository;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {
    private final WebClient client;
    private final TareaRepository tareaRepository;
    private final ConfiguracionAgendaRepository configuracionRepository;
    private final TareaService tareaService;

    public AgendaService(WebClient.Builder builder, TareaRepository tareaRepository,
                         ConfiguracionAgendaRepository configuracionRepository,
                         TareaService tareaService) {
        // Nota: "http://app-b" es el NOMBRE de la app registrada en Eureka
        this.client = builder.baseUrl("http://PROLOGAPI").build();
        this.tareaRepository = tareaRepository;
        this.configuracionRepository = configuracionRepository;
        this.tareaService = tareaService;
    }

    public Integer sum(int a, int b) {
        return client.get()
                .uri(uri -> uri.path("/api/sum").queryParam("a", a).queryParam("b", b).build())
                .retrieve()
                .bodyToMono(Integer.class)
                .block(); // bloqueante (simple)
    }

    /**
     * Genera un plan optimizado para el día llamando a PrologAPI
     */
    public PlanResponse generarPlan(PlanRequest request) {
        // Validar entrada
        validarRequest(request);

        // Obtener configuración del usuario
        if (request.getMinutosDisponibles() == null || request.getHoraInicio() == null) {
            ConfiguracionAgenda config = configuracionRepository
                    .findByUsuarioId(request.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe configuración para el usuario " + request.getUsuarioId()));

            if (request.getMinutosDisponibles() == null) {
                request.setMinutosDisponibles(config.getMinutosDisponibles());
            }
            if (request.getHoraInicio() == null) {
                request.setHoraInicio(config.getHoraInicio().toString());
            }
        }

        // Obtener tareas del día (solo pendientes y planificadas)
        List<Tarea> tareas = tareaRepository.findByUsuarioIdAndFecha(
                        request.getUsuarioId(),
                        request.getFecha()
                ).stream()
                .filter(t -> t.getEstado() == Tarea.Estado.PENDIENTE ||
                        t.getEstado() == Tarea.Estado.PLANIFICADA)
                .collect(Collectors.toList());

        // Transformar tareas a TaskDTO
        List<TaskDTO> tasks = tareas.stream()
                .map(this::tareaToTaskDTO)
                .collect(Collectors.toList());

        // Construir dependencias
        List<DepDTO> deps = tareas.stream()
                .filter(t -> t.getDependeDeId() != null)
                .map(t -> {
                    DepDTO dep = new DepDTO();
                    dep.setTarea(t.getId());
                    dep.setDependeDe(t.getDependeDeId());
                    return dep;
                })
                .collect(Collectors.toList());

        // Construir request para PrologAPI
        request.setTasks(tasks);
        request.setDeps(deps);

        // Llamar a PrologAPI
        PlanResponse response = llamarPrologAPI(request);

        // Si el plan es posible, actualizar estados de las tareas planificadas
        if (response.isPosible() && response.getTareasPlan() != null) {
            actualizarEstadosTareasPlanificadas(response);
        }

        // Agregar sugerencias si el plan no es posible
        if (!response.isPosible()) {
            response.setSugerencias(generarSugerencias(response, tareas));
        }

        return response;
    }

    /**
     * Replanifica el día considerando solo tareas PENDIENTES y PLANIFICADAS,
     * excluyendo las que ya están COMPLETADAS o CANCELADAS.
     * Permite ajustar el tiempo disponible restante y la hora actual.
     */
    public PlanResponse replanificar(PlanRequest request) {
        // Validar entrada
        validarRequest(request);

        // Obtener configuración del usuario si no se proporciona
        if (request.getMinutosDisponibles() == null || request.getHoraInicio() == null) {
            ConfiguracionAgenda config = configuracionRepository
                    .findByUsuarioId(request.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe configuración para el usuario " + request.getUsuarioId()));

            if (request.getMinutosDisponibles() == null) {
                request.setMinutosDisponibles(config.getMinutosDisponibles());
            }
            if (request.getHoraInicio() == null) {
                request.setHoraInicio(config.getHoraInicio().toString());
            }
        }

        // Obtener SOLO tareas PENDIENTES (excluyendo COMPLETADAS y CANCELADAS)
        // Esto permite replanificar solo lo que queda por hacer
        List<Tarea> tareasPendientes = tareaRepository.findByUsuarioIdAndFecha(
                        request.getUsuarioId(),
                        request.getFecha()
                ).stream()
                .filter(t -> t.getEstado() == Tarea.Estado.PENDIENTE ||
                        t.getEstado() == Tarea.Estado.PLANIFICADA)
                .collect(Collectors.toList());

        // Resetear estados PLANIFICADAS a PENDIENTE antes de replanificar
        tareasPendientes.stream()
                .filter(t -> t.getEstado() == Tarea.Estado.PLANIFICADA)
                .forEach(t -> {
                    t.setEstado(Tarea.Estado.PENDIENTE);
                    tareaService.actualizar(t.getId(), t);
                });

        // Transformar tareas a TaskDTO
        List<TaskDTO> tasks = tareasPendientes.stream()
                .map(this::tareaToTaskDTO)
                .collect(Collectors.toList());

        // Construir dependencias (solo de tareas pendientes)
        List<DepDTO> deps = tareasPendientes.stream()
                .filter(t -> t.getDependeDeId() != null)
                .map(t -> {
                    DepDTO dep = new DepDTO();
                    dep.setTarea(t.getId());
                    dep.setDependeDe(t.getDependeDeId());
                    return dep;
                })
                .collect(Collectors.toList());

        // Construir request para PrologAPI
        request.setTasks(tasks);
        request.setDeps(deps);

        // Llamar a PrologAPI
        PlanResponse response = llamarPrologAPI(request);

        // Si el plan es posible, actualizar estados de las tareas planificadas
        if (response.isPosible() && response.getTareasPlan() != null) {
            actualizarEstadosTareasPlanificadas(response);
        }

        // Agregar sugerencias si el plan no es posible
        if (!response.isPosible()) {
            response.setSugerencias(generarSugerencias(response, tareasPendientes));
        }

        return response;
    }

    /**
     * Convierte una entidad Tarea a TaskDTO
     */

    private TaskDTO tareaToTaskDTO(Tarea tarea) {
        TaskDTO dto = new TaskDTO();
        dto.setId(tarea.getId());
        dto.setNombre(tarea.getNombre());
        dto.setPrioridad(prioridadToInt(tarea.getPrioridad()));
        dto.setDur(tarea.getDuracionMinutos());

        // Convertir clima (puede ser null o una lista)
        if (tarea.getClimaPermitido() != null) {
            dto.setClimas(List.of(tarea.getClimaPermitido().name().toLowerCase()));
        } else {
            dto.setClimas(new ArrayList<>());
        }
        return dto;
    }

    /**
     * Convierte enum Prioridad a número
     */
    private int prioridadToInt(Tarea.Prioridad prioridad) {
        switch (prioridad) {
            case ALTA:
                return 3;
            case MEDIA:
                return 2;
            case BAJA:
                return 1;
            default:
                return 2;
        }
    }

    /**
     * Llama al endpoint /api/plan de PrologAPI
     */
    private PlanResponse llamarPrologAPI(PlanRequest request) {
        try {
            return client.post()
                    .uri("/api/plan")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PlanResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a PrologAPI: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el request tenga los datos mínimos necesarios
     */
    private void validarRequest(PlanRequest request) {
        if (request.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }
        if (request.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (request.getClimaDia() == null || request.getClimaDia().isBlank()) {
            throw new IllegalArgumentException("El clima del día es obligatorio");
        }
    }

    /**
     * Actualiza los estados de las tareas planificadas después de generar un plan exitoso
     */
    private void actualizarEstadosTareasPlanificadas(PlanResponse response) {
        if (response.getTareasPlan() == null || response.getTareasPlan().isEmpty()) {
            return;
        }

        // Crear un mapa de IDs planificados para búsqueda rápida
        java.util.Set<Long> idsPlanificados = new java.util.HashSet<>();
        for (SlotDTO slot : response.getTareasPlan()) {
            idsPlanificados.add(slot.getId());
        }

        // Obtener todas las tareas del día que fueron planificadas
        for (Long id : idsPlanificados) {
            try {
                Tarea tarea = tareaService.obtener(id);
                if (tarea != null && tarea.getEstado() == Tarea.Estado.PENDIENTE) {
                    // Actualizar solo las que están en PENDIENTE
                    tarea.setEstado(Tarea.Estado.PLANIFICADA);
                    tareaService.actualizar(id, tarea);
                }
            } catch (Exception e) {
                // Log error pero continuar con las demás
                System.err.println("Error actualizando estado de tarea " + id + ": " + e.getMessage());
            }
        }
    }

    /**
     * Genera sugerencias cuando el plan no es posible
     */
    private String generarSugerencias(PlanResponse response, List<Tarea> tareas) {
        StringBuilder sugerencias = new StringBuilder();
        sugerencias.append("No se pudo generar un plan completo. Sugerencias:\n\n");

        // 1. Revisar duración total vs tiempo disponible
        int duracionTotal = tareas.stream()
                .mapToInt(Tarea::getDuracionMinutos)
                .sum();
        int tiempoDisponible = 480; // 8 horas por defecto
        if (duracionTotal > tiempoDisponible) {
            sugerencias.append("• Reducir la duración de las tareas o eliminar algunas. ");
            sugerencias.append(String.format("Duración total: %d min > Tiempo disponible: %d min\n",
                    duracionTotal, tiempoDisponible));
        }

        // 2. Revisar incompatibilidades de clima
        long tareasConClima = tareas.stream()
                .filter(t -> t.getClimaPermitido() != null)
                .count();
        if (tareasConClima > 0) {
            sugerencias.append("• Verificar que el clima del día permita realizar todas las tareas.\n");
        }

        // 3. Revisar dependencias bloqueadas
        long tareasConDependencias = tareas.stream()
                .filter(t -> t.getDependeDeId() != null)
                .count();
        if (tareasConDependencias > 0) {
            sugerencias.append("• Algunas tareas dependientes podrían no completarse. ");
            sugerencias.append("Considerar marcar las tareas padre como COMPLETADAS primero.\n");
        }

        // 4. Sugerencia general
        sugerencias.append("• Intente reducir prioridades de algunas tareas o postponerlas para otro día.\n");

        return sugerencias.toString();
    }
}