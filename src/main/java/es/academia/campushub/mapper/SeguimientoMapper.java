package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.SeguimientoResponse;
import es.academia.campushub.document.SeguimientoDocument;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;


/** Convierte documentos de Mongo en DTO de respuesta. */
@Component
public class SeguimientoMapper {

    //Documento de Mongo -> DTO de respuesta (lo que devolvemos en la API)
    public SeguimientoResponse toResponse (SeguimientoDocument doc) {
        return new SeguimientoResponse(
                doc.getId(),
                doc.getActividadId(),
                doc.getTipo(),
                doc.getDatos() == null ? new HashMap<>() : doc.getDatos(), //Si los datos vienen en null, devolveremos un HasMap para q no falle ni salga null en ej json
                doc.getCreadoEn() == null ? null : doc.getCreadoEn().toString()); //Pasamos la fecha a String para el DTO, en caso de ser null la dejamos para q no salga una excepcion de NullPointerException
    }

    // Lo mismo que el metodo de arriba, pero para una lista de documentos
    public List<SeguimientoResponse> toResponseList (List<SeguimientoDocument> docs) {
        if (docs == null) {
            return List.of(); // si la lista es null devolvemos una lista vacía en vez de romper
        }

        return docs.stream().map(this::toResponse).toList();  // recorremos cada documento, lo convertimos a SeguimientoResponse con toResponse y lo metemos en una lista nueva


    }
}