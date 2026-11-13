package es.academia.campushub.repository;

import es.academia.campushub.entity.EtiquetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtiquetaRepository extends JpaRepository<EtiquetaEntity, Long> {
    boolean existsByNombre(String nombre);
}