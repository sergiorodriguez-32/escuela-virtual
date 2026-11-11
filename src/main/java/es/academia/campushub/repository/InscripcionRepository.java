package es.academia.campushub.repository;

import es.academia.campushub.entity.InscripcionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscripcionRepository extends JpaRepository<InscripcionEntity, Long> {

  boolean existsByActividad_IdActividadAndEstudiante_IdEstudiante(
      Long actividadId, Long estudianteId);

  long countByActividad_IdActividad(Long actividadId);

  @Query(
      "select i from InscripcionEntity i join fetch i.estudiante e join fetch e.grupoEstudiante "
          + "where i.actividad.idActividad = :actividadId order by i.fechaInscripcion, i.idInscripcion")
  List<InscripcionEntity> findParticipantes(@Param("actividadId") Long actividadId);
}
