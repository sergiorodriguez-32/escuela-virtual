package es.academia.campushub.repository;

import es.academia.campushub.document.SeguimientoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeguimientoRepository extends MongoRepository<SeguimientoDocument, String> {
    List<SeguimientoDocument> findByActividadIdOrderByCreadoEnDesc(Long actividadId);
}
