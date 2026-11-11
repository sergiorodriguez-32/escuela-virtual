package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "estudiante",
        uniqueConstraints = @UniqueConstraint(name = "uk_estudiante_correo", columnNames = "correo_estudiante"),
        indexes = @Index(name = "idx_estudiante_grupo", columnList = "grupo_id"))
@Getter
@Setter
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstudiante;

    @Column(nullable = false, length = 100)
    private String nombreEstudiante;

    @Column(nullable = false, length = 150)
    private String apellidoEstudiante;

    @Column(nullable = false, length = 150)
    private String correoEstudiante;

    @Column(nullable = false,
            check = @CheckConstraint(name = "ck_estudiante_nacimiento_pasado",
                    constraint = "fecha_nacimiento_estudiante < CURRENT_DATE"))
    private LocalDate fechaNacimientoEstudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_estudiante_grupo"))
    private GrupoEntity grupoEstudiante;
}
