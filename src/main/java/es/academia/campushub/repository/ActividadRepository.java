package es.academia.campushub.repository;

import es.academia.campushub.entity.ActividadEntity;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActividadRepository extends JpaRepository<ActividadEntity, Long> {

  /** Bloquea la fila de la actividad (SELECT ... FOR UPDATE) para serializar las inscripciones. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from ActividadEntity a where a.idActividad = :id")
  Optional<ActividadEntity> findByIdForUpdate(@Param("id") Long id);

  @Query("select a from ActividadEntity a join a.etiquetas e "
          + "where e.idEtiqueta = :etiquetaId order by a.idActividad")
  List<ActividadEntity> findByEtiqueta(@Param("etiquetaId") Long etiquetaId);
}
