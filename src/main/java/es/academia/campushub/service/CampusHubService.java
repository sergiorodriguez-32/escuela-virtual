package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import java.util.List;

/** Contrato de los casos de uso de CampusHub. */
public interface CampusHubService {
  GrupoResponse crearGrupo(CreateGrupoRequest request);

  DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request);

  EstudianteResponse crearEstudiante(CreateEstudianteRequest request);

  EstudianteResponse obtenerEstudiante(Long id);

  DocenteResponse crearDocente(CreateDocenteRequest request);

  ActividadResponse crearActividad(CreateActividadRequest request);

  ActividadResponse obtenerActividad(Long id);

  InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request);

  List<EstudianteResponse> participantes(Long actividadId);

  SeguimientoResponse registrarSeguimiento(Long actividadId, CreateSeguimientoRequest request);

  ActividadDetalleResponse detalle(Long id);

  List<ActividadHistorialResponse> historial(Long estudianteId);

  ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request);

  ContactoResponse contacto(Long estudianteId);

  EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request);

  void asociarEtiqueta(Long actividadId, Long etiquetaId);

  List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId);
}
