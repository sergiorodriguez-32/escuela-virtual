package es.academia.campushub;

import es.academia.campushub.support.ApiIntegrationSupport;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AforoConcurrenciaIT extends ApiIntegrationSupport {

    @Test
    void elAforoNoSeSuperConInscripcionesSimultaneas() throws Exception {
        int aforo = 5;
        int intentos = 20;

        long dept = id(post("/api/departamentos", "{\"nombre\":\"Concurrencia\"}"));
        long docente = id(post("/api/docentes",
                "{\"nombre\":\"Ana\",\"apellidos\":\"Aforo\",\"email\":\"aforo.it@test.invalid\","
                        + "\"departamentoId\":" + dept + "}"));
        long grupo = id(post("/api/grupos",
                "{\"nombre\":\"DAM2-AFORO\",\"nivel\":\"2 DAM\",\"tutorDocenteId\":" + docente + "}"));
        long actividad = id(post("/api/actividades",
                "{\"titulo\":\"Taller aforo\",\"descripcion\":\"Concurrencia\",\"tipo\":\"TALLER\","
                        + "\"fechaPrevista\":\"2027-03-01\",\"maxParticipantes\":" + aforo
                        + ",\"docenteId\":" + docente + "}"));
        List<Long> estudiantes = new ArrayList<>();
        for (int i = 0; i < intentos; i++) {
            estudiantes.add(id(post("/api/estudiantes",
                    "{\"nombre\":\"Est\",\"apellidos\":\"N" + i + "\",\"email\":\"aforo" + i
                            + ".it@test.invalid\",\"fechaNacimiento\":\"2005-01-01\","
                            + "\"grupoId\":" + grupo + "}")));

        }

        // 20 hilos esperan a la misma señal y se inscriben a la vez
        ExecutorService pool = Executors.newFixedThreadPool(intentos);
        CountDownLatch salida = new CountDownLatch(1);
        List<Future<HttpResponse<String>>> respuestas = new ArrayList<>();
        for (Long est : estudiantes) {
            respuestas.add(pool.submit(() -> {
                salida.await();
                return post("/api/actividades/" + actividad + "/inscripciones",
                        "{\"estudianteId\":" + est + "}");
            }));
        }
        salida.countDown(); // ¡ya!
        int creadas = 0;
        int rechazadas = 0;
        for (Future<HttpResponse<String>> f : respuestas) {
            int codigo = f.get(30, TimeUnit.SECONDS).statusCode();
            if (codigo == 201) {
                creadas++;
            } else if (codigo == 409) {
                rechazadas++;
            }
        }
        pool.shutdown();
        assertEquals(aforo, creadas, "Inscripciones aceptadas");
        assertEquals(intentos - aforo, rechazadas, "Inscripciones rechazadas por aforo");
        var detalle = get("/api/actividades/" + actividad + "/detalle");
        assertTrue(detalle.body().contains("\"plazasDisponibles\":0"), detalle.body());
    }

}
