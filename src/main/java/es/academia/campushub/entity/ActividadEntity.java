package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/** Actividad con fecha y aforo máximo, a cargo de un docente. */
@Entity
@Table(name = "actividad",
        indexes = @Index(name = "idx_actividad_docente", columnList = "docente_responsable_id"))
@Getter
@Setter
public class ActividadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActividad; // clave primaria autogenerada

    @Column(nullable = false, length = 200)
    private String titulo; // título de la actividad

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion; // descripción larga (TEXT)

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoActividad tipoActividad; // tipo, guardado como texto

    @Column(nullable = false)
    private LocalDate fechaPrevista; // día en que se hará

    @Column(name = "numero_maximo_participantes",
            nullable = false,
            check = @CheckConstraint(name = "ck_actividad_max_positivo",
                    constraint = "numero_maximo_participantes > 0"))
    private Integer numeroMaximoParticipantes; // aforo máximo (debe ser > 0)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_responsable_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_actividad_docente"))
    private DocenteEntity docenteResponsable; // docente a cargo (muchas actividades -> un docente)
}
