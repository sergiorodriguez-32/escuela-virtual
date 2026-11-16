package es.academia.campushub.repository;

import es.academia.campushub.entity.DepartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de departamentos (CRUD básico).
public interface DepartamentoRepository extends JpaRepository<DepartamentoEntity, Long> {}
