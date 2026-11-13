package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "etiqueta",
        uniqueConstraints = @UniqueConstraint(name = "uk_etiqueta_nombre", columnNames = "nombre"))
@Getter
@Setter
public class EtiquetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEtiqueta;

    @Column(nullable = false, length = 50)
    private String nombre;
}