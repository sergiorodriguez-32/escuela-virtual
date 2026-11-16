package es.academia.campushub.document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Seguimiento de una actividad guardado en MongoDB. */
@Document(collection = "seguimientos")
@Data
@NoArgsConstructor
public class SeguimientoDocument {

    @Id
    private String id; // id generado por Mongo

    @Indexed(name = "idx_seguimiento_actividad") //Indice por actividadID
    private Long actividadId;

    private String tipo; // tipo de seguimiento

    private Instant creadoEn; // fecha de creación

    private Map<String, Object> datos = new HashMap<>(); // datos libres del seguimiento
}