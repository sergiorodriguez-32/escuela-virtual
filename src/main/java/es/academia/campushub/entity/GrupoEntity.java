package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grupo",
        indexes = @Index(name = "idx_grupo_tutor", columnList = "tutor_id"))
@Getter
@Setter
public class GrupoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;

    @Column(nullable = false, length = 100)
    private String nombreGrupo;

    @Column(nullable = false, length = 50)
    private String nivelGrupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_grupo_tutor"))
    private DocenteEntity tutor;
}
