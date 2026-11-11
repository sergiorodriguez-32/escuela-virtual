package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

/** Ficha de contacto 1:1. La clave primaria es la del estudiante, así que no puede haber dos ni existir sin él. */
@Entity
@Table(name = "contacto")
@Getter
@Setter
public class ContactoEntity {

    @Id
    @Column(name = "estudiante_id")
    private Long idEstudiante;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "estudiante_id", foreignKey = @ForeignKey(name = "fk_contacto_estudiante"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EstudianteEntity estudiante;

    @Column(nullable = false, length = 150)
    private String tutorLegal;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(nullable = false)
    private Boolean autorizado;

    private LocalDate fechaAutorizacion;
}
