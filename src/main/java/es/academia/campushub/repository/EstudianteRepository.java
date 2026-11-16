package es.academia.campushub.repository;

import es.academia.campushub.entity.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de estudiantes (CRUD básico).
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {}
