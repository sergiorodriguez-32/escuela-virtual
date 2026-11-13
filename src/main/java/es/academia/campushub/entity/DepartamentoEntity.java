package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Departamento al que pertenecen los docentes. */
@Entity
@Table(name = "departamento",
        uniqueConstraints = @UniqueConstraint(name = "uk_departamento_nombre", columnNames = "nombre_departamento"))
@Getter
@Setter
public class DepartamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartamento;

    @Column(nullable = false, length = 100)
    private String nombreDepartamento;
}
