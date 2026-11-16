package es.academia.campushub.repository;

import es.academia.campushub.entity.GrupoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de grupos (CRUD básico).
public interface GrupoRepository extends JpaRepository<GrupoEntity, Long> {}
