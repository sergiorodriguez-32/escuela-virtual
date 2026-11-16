package es.academia.campushub.repository;

import es.academia.campushub.entity.DocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de docentes (CRUD básico).
public interface DocenteRepository extends JpaRepository<DocenteEntity, Long> {}
