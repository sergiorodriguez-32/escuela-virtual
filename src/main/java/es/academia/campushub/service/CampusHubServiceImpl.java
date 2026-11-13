package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.document.SeguimientoDocument;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.EntityMapper;
import es.academia.campushub.mapper.SeguimientoMapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
import es.academia.campushub.util.exception.ResourceNotFoundException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de CampusHub. Las reglas de integridad (unicidad, claves foráneas, CHECK) las
 * garantiza la base de datos; aquí solo se validan los datos de entrada para devolver 400/404 con
 * mensajes claros, y se traducen los conflictos a 409.
 */
@Service
@Transactional
public class CampusHubServiceImpl implements CampusHubService {

    private final GrupoRepository grupos;
    private final DepartamentoRepository departamentos;
    private final DocenteRepository docentes;
    private final EstudianteRepository estudiantes;
    private final ActividadRepository actividades;
    private final InscripcionRepository inscripciones;
    private final EntityMapper mapper;
    private final SeguimientoRepository seguimientos;
    private final SeguimientoMapper seguimientoMapper;
    private final EtiquetaRepository etiquetas;
    private final ContactoRepository contactos;


    public CampusHubServiceImpl(
            GrupoRepository grupos,
            DepartamentoRepository departamentos,
            DocenteRepository docentes,
            EstudianteRepository estudiantes,
            ActividadRepository actividades,
            InscripcionRepository inscripciones,
            EntityMapper mapper,
            SeguimientoRepository seguimientos,
            SeguimientoMapper seguimientoMapper,
            EtiquetaRepository etiquetas,
            ContactoRepository contactos) {
        this.grupos = grupos;
        this.departamentos = departamentos;
        this.docentes = docentes;
        this.estudiantes = estudiantes;
        this.actividades = actividades;
        this.inscripciones = inscripciones;
        this.mapper = mapper;
        this.seguimientos = seguimientos;
        this.seguimientoMapper = seguimientoMapper;
        this.etiquetas = etiquetas;
        this.contactos = contactos;
  }

  // ---------- RF01 / RF02 / RF03 ----------

  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest r) {
    requireBody(r);
    GrupoEntity g = new GrupoEntity();
    g.setNombreGrupo(text(r.nombre(), "nombre", 100));
    g.setNivelGrupo(text(r.nivel(), "nivel", 50));
    g.setTutor(docente(r.tutorDocenteId()));
    return mapper.toResponse(grupos.saveAndFlush(g));
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest r) {
    requireBody(r);
    DepartamentoEntity d = new DepartamentoEntity();
    d.setNombreDepartamento(text(r.nombre(), "nombre", 100));
    return mapper.toResponse(departamentos.saveAndFlush(d));
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest r) {
    requireBody(r);
    DocenteEntity d = new DocenteEntity();
    d.setNombreDocente(text(r.nombre(), "nombre", 100));
    d.setApellidoDocente(text(r.apellidos(), "apellidos", 150));
    d.setCorreoDocente(email(r.email()));
    if (r.departamentoId() == null) throw new InvalidInputException("departamentoId es obligatorio");
    d.setDepartamento(
        departamentos
            .findById(r.departamentoId())
            .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado")));
    return mapper.toResponse(docentes.saveAndFlush(d));
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest r) {
    requireBody(r);
    EstudianteEntity e = new EstudianteEntity();
    e.setNombreEstudiante(text(r.nombre(), "nombre", 100));
    e.setApellidoEstudiante(text(r.apellidos(), "apellidos", 150));
    e.setCorreoEstudiante(email(r.email()));
    LocalDate nacimiento = date(r.fechaNacimiento(), "fechaNacimiento");
    if (!nacimiento.isBefore(LocalDate.now()))
      throw new InvalidInputException("fechaNacimiento debe ser anterior a hoy");
    e.setFechaNacimientoEstudiante(nacimiento);
    if (r.grupoId() == null) throw new InvalidInputException("grupoId es obligatorio");
    e.setGrupoEstudiante(
        grupos
            .findById(r.grupoId())
            .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado")));
    return mapper.toResponse(estudiantes.saveAndFlush(e));
  }

  @Override
  @Transactional(readOnly = true)
  public EstudianteResponse obtenerEstudiante(Long id) {
    return mapper.toResponse(estudiante(id));
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest r) {
    requireBody(r);
    ActividadEntity a = new ActividadEntity();
    a.setTitulo(text(r.titulo(), "titulo", 200));
    a.setDescripcion(text(r.descripcion(), "descripcion", Integer.MAX_VALUE));
    a.setTipoActividad(tipo(r.tipo()));
    a.setFechaPrevista(date(r.fechaPrevista(), "fechaPrevista"));
    if (r.maxParticipantes() == null || r.maxParticipantes() <= 0)
      throw new InvalidInputException("maxParticipantes debe ser mayor que 0");
    a.setNumeroMaximoParticipantes(r.maxParticipantes());
    a.setDocenteResponsable(docente(r.docenteId()));
    return mapper.toResponse(actividades.saveAndFlush(a));
  }

  @Override
  @Transactional(readOnly = true)
  public ActividadResponse obtenerActividad(Long id) {
    return mapper.toResponse(actividad(id));
  }

  // ---------- RF04 ----------

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest r) {
    requireBody(r);
    if (r.estudianteId() == null) throw new InvalidInputException("estudianteId es obligatorio");
    // Bloqueo de fila: dos inscripciones concurrentes a la misma actividad se serializan y el
    // aforo no puede superarse. La UNIQUE (actividad, estudiante) protege además los duplicados.
    ActividadEntity actividad =
        actividades
            .findByIdForUpdate(actividadId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    EstudianteEntity estudiante = estudiante(r.estudianteId());
    if (inscripciones.existsByActividad_IdActividadAndEstudiante_IdEstudiante(
        actividadId, r.estudianteId()))
      throw new BusinessConflictException("El estudiante ya está inscrito en la actividad");
    if (inscripciones.countByActividad_IdActividad(actividadId)
        >= actividad.getNumeroMaximoParticipantes())
      throw new BusinessConflictException("La actividad no tiene plazas disponibles");
    InscripcionEntity i = new InscripcionEntity();
    i.setActividad(actividad);
    i.setEstudiante(estudiante);
    i.setFechaInscripcion(LocalDateTime.now());
    return mapper.toResponse(inscripciones.saveAndFlush(i));
  }

  @Override
  @Transactional(readOnly = true)
  public List<EstudianteResponse> participantes(Long actividadId) {
    actividad(actividadId);
    return inscripciones.findParticipantes(actividadId).stream()
        .map(i -> mapper.toResponse(i.getEstudiante()))
        .toList();
  }

  // ---------- Pendiente de otros miembros del equipo ----------

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
        requireBody(request);
        String tipo = text(request.tipo(), "tipo", 50 );
        if (!actividades.existsById(actividadId)){
            throw new ResourceNotFoundException("Actividad no encontrada");
    }
      SeguimientoDocument doc = new SeguimientoDocument();
        doc.setActividadId(actividadId);
        doc.setTipo(tipo);
        doc.setCreadoEn(Instant.now()); //fecha puesta pr el servirdor
        doc.setDatos(request.datos() == null ? new HashMap<>() : new HashMap<>(request.datos()));
        return seguimientoMapper.toResponse(seguimientos.save(doc));
  }

  @Override
  @Transactional (readOnly = true)
  public ActividadDetalleResponse detalle(Long id) {
        ActividadEntity actividad = actividad(id);

        List<EstudianteResponse> inscritos =
                inscripciones.findParticipantes(id).stream()
                        .map (i -> mapper.toResponse(i.getEstudiante()))
                        .toList();
                int ocupadas = inscritos.size();
                int disponibles = Math.max(0, actividad.getNumeroMaximoParticipantes() - ocupadas);

                List <SeguimientoResponse> listaSeguimientos;
                try {
                    listaSeguimientos =
                            seguimientoMapper.toResponseList(seguimientos.findByActividadIdOrderByCreadoEnDesc(id));
                } catch (DataAccessException ex) {
                    listaSeguimientos = List.of();
                }
    return new ActividadDetalleResponse(
            mapper.toResponse(actividad),
            mapper.toResponse(actividad.getDocenteResponsable()),
            ocupadas,
            disponibles,
            inscritos,
            List.of(),
            listaSeguimientos);

  }

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    estudiante(estudianteId);
    return inscripciones.findHistorial(estudianteId).stream()
            .map(mapper::toHistorialResponse)
            .toList();
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    throw new PersistenciaPendienteException("guardarContacto");
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    throw new PersistenciaPendienteException("contacto");
  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest r) {
    requireBody(r);
    String nombre = text(r.nombre(), "nombre", 50).toLowerCase();
    if (etiquetas.existsByNombre(nombre))
      throw new BusinessConflictException("Ya existe una etiqueta con ese nombre");
    EtiquetaEntity e = new EtiquetaEntity();
    e.setNombre(nombre);
    return mapper.toResponse(etiquetas.saveAndFlush(e));
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    ActividadEntity a = actividad(actividadId);
    EtiquetaEntity e =
            etiquetas
                    .findById(etiquetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));
    a.getEtiquetas().add(e);
    actividades.saveAndFlush(a);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    if (!etiquetas.existsById(etiquetaId))
      throw new ResourceNotFoundException("Etiqueta no encontrada");
    return actividades.findByEtiqueta(etiquetaId).stream().map(mapper::toResponse).toList();
  }

  // ---------- Utilidades ----------

  private DocenteEntity docente(Long id) {
    if (id == null) throw new InvalidInputException("El docente es obligatorio");
    return docentes
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
  }

  private EstudianteEntity estudiante(Long id) {
    return estudiantes
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
  }

  private ActividadEntity actividad(Long id) {
    return actividades
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
  }

  private static void requireBody(Object body) {
    if (body == null) throw new InvalidInputException("El cuerpo de la petición es obligatorio");
  }

  private static String text(String value, String field, int max) {
    if (value == null || value.isBlank()) throw new InvalidInputException(field + " es obligatorio");
    String v = value.strip();
    if (v.length() > max) throw new InvalidInputException(field + " supera " + max + " caracteres");
    return v;
  }

  private static String email(String value) {
    String v = text(value, "email", 150);
    if (!v.matches("^[^@\\s]+@[^@\\s]+$")) throw new InvalidInputException("email no válido");
    return v;
  }

  private static LocalDate date(String value, String field) {
    if (value == null || value.isBlank()) throw new InvalidInputException(field + " es obligatorio");
    try {
      return LocalDate.parse(value.strip());
    } catch (DateTimeParseException ex) {
      throw new InvalidInputException(field + " debe tener formato AAAA-MM-DD");
    }
  }

  private static TipoActividad tipo(String value) {
    if (value == null || value.isBlank()) throw new InvalidInputException("tipo es obligatorio");
    try {
      return TipoActividad.valueOf(value.strip().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidInputException("tipo no válido (TALLER, CHARLA, VISITA o JORNADA)");
    }
  }
}
