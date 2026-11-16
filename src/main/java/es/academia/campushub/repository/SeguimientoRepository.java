package es.academia.campushub.repository;

import es.academia.campushub.document.SeguimientoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// Acceso a los seguimientos guardados en MongoDB.
public interface SeguimientoRepository extends MongoRepository<SeguimientoDocument, String> {
    // Seguimientos de una actividad, del más nuevo al más antiguo
    List<SeguimientoDocument> findByActividadIdOrderByCreadoEnDesc(Long actividadId);
}
