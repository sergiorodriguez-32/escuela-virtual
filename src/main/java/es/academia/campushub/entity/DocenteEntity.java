package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Docente adscrito a un departamento. */
@Entity
@Table(name = "docente",
        uniqueConstraints = @UniqueConstraint(name = "uk_docente_correo", columnNames = "correo_docente"),
        indexes = @Index(name = "idx_docente_departamento", columnList = "departamento_id"))
@Getter
@Setter
public class DocenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocente; // clave primaria autogenerada

    @Column(nullable = false, length = 100)
    private String nombreDocente; // nombre

    @Column(nullable = false, length = 150)
    private String apellidoDocente; // apellidos

    @Column(nullable = false, length = 150)
    private String correoDocente; // correo (único)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_docente_departamento"))
    private DepartamentoEntity departamento; // departamento al que pertenece
}
