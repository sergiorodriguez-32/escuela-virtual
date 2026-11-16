package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/** Estudiante matriculado en un grupo. */
@Entity
@Table(name = "estudiante",
        uniqueConstraints = @UniqueConstraint(name = "uk_estudiante_correo", columnNames = "correo_estudiante"),
        indexes = @Index(name = "idx_estudiante_grupo", columnList = "grupo_id"))
@Getter
@Setter
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstudiante; // clave primaria autogenerada

    @Column(nullable = false, length = 100)
    private String nombreEstudiante; // nombre

    @Column(nullable = false, length = 150)
    private String apellidoEstudiante; // apellidos

    @Column(nullable = false, length = 150)
    private String correoEstudiante; // correo (único)

    @Column(nullable = false,
            check = @CheckConstraint(name = "ck_estudiante_nacimiento_pasado",
                    constraint = "fecha_nacimiento_estudiante < CURRENT_DATE"))
    private LocalDate fechaNacimientoEstudiante; // fecha de nacimiento (anterior a hoy)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_estudiante_grupo"))
    private GrupoEntity grupoEstudiante; // grupo al que pertenece
}
