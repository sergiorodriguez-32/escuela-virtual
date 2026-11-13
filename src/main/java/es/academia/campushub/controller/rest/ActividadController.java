package es.academia.campushub.controller.rest;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.service.CampusHubService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de actividades, inscripciones y seguimiento. */
@RestController
public class ActividadController {
  private final CampusHubService service;

  public ActividadController(CampusHubService service) {
    this.service = service;
  }

  @PostMapping("/api/actividades")
  public ResponseEntity<ActividadResponse> crear(@RequestBody CreateActividadRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearActividad(r));
  }

  @GetMapping("/api/actividades/{id}")
  public ActividadResponse obtener(@PathVariable Long id) {
    return service.obtenerActividad(id);
  }

  @PostMapping("/api/actividades/{id}/inscripciones")
  public ResponseEntity<InscripcionResponse> inscribir(
      @PathVariable Long id, @RequestBody CreateInscripcionRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.inscribir(id, r));
  }

  @GetMapping("/api/actividades/{id}/inscripciones")
  public List<EstudianteResponse> participantes(@PathVariable Long id) {
    return service.participantes(id);
  }

  @PostMapping("/api/actividades/{id}/seguimientos")
  public ResponseEntity<SeguimientoResponse> seguimiento(
      @PathVariable Long id, @RequestBody CreateSeguimientoRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarSeguimiento(id, r));
  }

  @GetMapping("/api/actividades/{id}/detalle")
  public ActividadDetalleResponse detalle(@PathVariable Long id) {
    return service.detalle(id);
  }
}
