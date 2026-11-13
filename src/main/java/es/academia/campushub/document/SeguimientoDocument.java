package es.academia.campushub.document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "seguimientos")
@Data
@NoArgsConstructor
public class SeguimientoDocument {

    @Id
    private String id;

    @Indexed(name = "idx_seguimiento_actividad") //Indice por actividadID
    private Long actividadId;

    private String tipo;

    private Instant creadoEn;

    private Map<String, Object> datos = new HashMap<>();
}