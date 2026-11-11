package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "actividad",
        indexes = @Index(name = "idx_actividad_docente", columnList = "docente_responsable_id"))
@Getter
@Setter
public class ActividadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActividad;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoActividad tipoActividad;

    @Column(nullable = false)
    private LocalDate fechaPrevista;

    @Column(name = "numero_maximo_participantes",
            nullable = false,
            check = @CheckConstraint(name = "ck_actividad_max_positivo",
                    constraint = "numero_maximo_participantes > 0"))
    private Integer numeroMaximoParticipantes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_responsable_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_actividad_docente"))
    private DocenteEntity docenteResponsable;
}
