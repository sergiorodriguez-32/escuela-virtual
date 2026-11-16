package es.academia.campushub.repository;

import es.academia.campushub.entity.InscripcionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Acceso a datos de inscripciones.
public interface InscripcionRepository extends JpaRepository<InscripcionEntity, Long> {

  // Comprueba si el estudiante ya está inscrito en la actividad
  boolean existsByActividad_IdActividadAndEstudiante_IdEstudiante(
      Long actividadId, Long estudianteId);

  // Cuenta cuántos inscritos tiene la actividad
  long countByActividad_IdActividad(Long actividadId);

  // Estudiantes inscritos en una actividad, ordenados por fecha de inscripción
  @Query(
      "select i from InscripcionEntity i join fetch i.estudiante e join fetch e.grupoEstudiante "
          + "where i.actividad.idActividad = :actividadId order by i.fechaInscripcion, i.idInscripcion")
  List<InscripcionEntity> findParticipantes(@Param("actividadId") Long actividadId);

  //Historial estudiante
  @Query(
          "select i from InscripcionEntity i join fetch i.estudiante e join fetch e.grupoEstudiante "
                  + "where i.estudiante.idEstudiante = :estudianteId order by i.fechaInscripcion desc")
    List<InscripcionEntity> findHistorial (@Param("estudianteId") Long estudianteId);
}
