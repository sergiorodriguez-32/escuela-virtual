package es.academia.campushub.repository;

import es.academia.campushub.entity.ContactoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de contactos (CRUD básico).
public interface ContactoRepository extends JpaRepository<ContactoEntity, Long> {}
