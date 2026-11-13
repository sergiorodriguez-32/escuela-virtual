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
    private Long idDocente;

    @Column(nullable = false, length = 100)
    private String nombreDocente;

    @Column(nullable = false, length = 150)
    private String apellidoDocente;

    @Column(nullable = false, length = 150)
    private String correoDocente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_docente_departamento"))
    private DepartamentoEntity departamento;
}
