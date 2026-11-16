package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import java.util.List;

/** Contrato de los casos de uso de CampusHub. */
public interface CampusHubService {
  // Crea un grupo con su tutor
  GrupoResponse crearGrupo(CreateGrupoRequest request);

  // Crea un departamento
  DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request);

  // Crea un estudiante en un grupo
  EstudianteResponse crearEstudiante(CreateEstudianteRequest request);

  // Busca un estudiante por id
  EstudianteResponse obtenerEstudiante(Long id);

  // Crea un docente en un departamento
  DocenteResponse crearDocente(CreateDocenteRequest request);

  // Crea una actividad
  ActividadResponse crearActividad(CreateActividadRequest request);

  // Busca una actividad por id
  ActividadResponse obtenerActividad(Long id);

  // Inscribe un estudiante (comprueba duplicados y aforo)
  InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request);

  // Lista los estudiantes inscritos en una actividad
  List<EstudianteResponse> participantes(Long actividadId);

  // Guarda un seguimiento de la actividad (Mongo)
  SeguimientoResponse registrarSeguimiento(Long actividadId, CreateSeguimientoRequest request);

  // Detalle completo: docente, plazas, inscritos y seguimientos
  ActividadDetalleResponse detalle(Long id);

  // Actividades en las que ha participado un estudiante
  List<ActividadHistorialResponse> historial(Long estudianteId);

  // Crea o actualiza el contacto del estudiante
  ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request);

  // Obtiene el contacto del estudiante
  ContactoResponse contacto(Long estudianteId);

  // Crea una etiqueta
  EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request);

  // Asocia una etiqueta a una actividad
  void asociarEtiqueta(Long actividadId, Long etiquetaId);

  // Actividades que tienen una etiqueta
  List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId);
}
