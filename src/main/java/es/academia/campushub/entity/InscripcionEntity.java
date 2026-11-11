package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscripcion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inscripcion_actividad_estudiante",
                columnNames = {"actividad_id", "estudiante_id"}),
        indexes = @Index(name = "idx_inscripcion_estudiante", columnList = "estudiante_id"))
@Getter
@Setter
public class InscripcionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_inscripcion_actividad"))
    private ActividadEntity actividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_inscripcion_estudiante"))
    private EstudianteEntity estudiante;

    @Column(nullable = false)
    private LocalDateTime fechaInscripcion;
}
