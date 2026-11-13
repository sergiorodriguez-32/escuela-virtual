package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import org.springframework.stereotype.Component;

/** Conversión manual entre entidades JPA y DTO de la API. */
@Component
public class EntityMapper {

    public DepartamentoResponse toResponse(DepartamentoEntity e) {
        return new DepartamentoResponse(e.getIdDepartamento(), e.getNombreDepartamento());
    }

    public DocenteResponse toResponse(DocenteEntity e) {
        return new DocenteResponse(
                e.getIdDocente(),
                e.getNombreDocente(),
                e.getApellidoDocente(),
                e.getCorreoDocente(),
                e.getDepartamento().getIdDepartamento());
    }

    public GrupoResponse toResponse(GrupoEntity e) {
        return new GrupoResponse(
                e.getIdGrupo(), e.getNombreGrupo(), e.getNivelGrupo(), e.getTutor().getIdDocente());
    }

    public EstudianteResponse toResponse(EstudianteEntity e) {
        return new EstudianteResponse(
                e.getIdEstudiante(),
                e.getNombreEstudiante(),
                e.getApellidoEstudiante(),
                e.getCorreoEstudiante(),
                e.getFechaNacimientoEstudiante().toString(),
                e.getGrupoEstudiante().getIdGrupo());
    }

    public ActividadResponse toResponse(ActividadEntity e) {
        return new ActividadResponse(
                e.getIdActividad(),
                e.getTitulo(),
                e.getDescripcion(),
                e.getTipoActividad().name(),
                e.getFechaPrevista().toString(),
                e.getNumeroMaximoParticipantes(),
                e.getDocenteResponsable().getIdDocente());
    }

    public InscripcionResponse toResponse(InscripcionEntity e) {
        return new InscripcionResponse(
                e.getIdInscripcion(),
                e.getActividad().getIdActividad(),
                e.getEstudiante().getIdEstudiante(),
                e.getFechaInscripcion().toString());
    }

    public ContactoResponse toResponse(ContactoEntity e) {
        return new ContactoResponse(
                e.getIdEstudiante(),
                e.getTutorLegal(),
                e.getTelefono(),
                e.getCorreo(),
                e.getAutorizado(),
                e.getFechaAutorizacion() == null ? null : e.getFechaAutorizacion().toString());
    }

    public EtiquetaResponse toResponse(EtiquetaEntity e) {
        return new EtiquetaResponse(e.getIdEtiqueta(), e.getNombre());
    }

    //Historial
    public ActividadHistorialResponse toHistorialResponse(InscripcionEntity i) {
        ActividadEntity a = i.getActividad();
        return new ActividadHistorialResponse(
                a.getIdActividad(),
                a.getTitulo(),
                a.getTipoActividad().name(),
                a.getFechaPrevista().toString(),
                i.getFechaInscripcion().toString(),
                toResponse(a.getDocenteResponsable()));

    }
}