package es.academia.campushub.controller.rest;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.service.CampusHubService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de estudiantes y su contacto. */
@RestController
public class EstudianteController {
  private final CampusHubService uc;

  public EstudianteController(CampusHubService uc) {
    this.uc = uc;
  }

  @PostMapping("/api/estudiantes")
  public ResponseEntity<EstudianteResponse> crear(@RequestBody CreateEstudianteRequest r) {
    return ResponseEntity.status(201).body(uc.crearEstudiante(r));
  }

  @GetMapping("/api/estudiantes/{id}")
  public EstudianteResponse obtener(@PathVariable Long id) {
    return uc.obtenerEstudiante(id);
  }

  @GetMapping("/api/estudiantes/{id}/actividades")
  public List<ActividadHistorialResponse> historial(@PathVariable Long id) {
    return uc.historial(id);
  }

  @PutMapping("/api/estudiantes/{id}/contacto")
  public ContactoResponse contactoGuardar(
      @PathVariable Long id, @RequestBody UpsertContactoRequest r) {
    return uc.guardarContacto(id, r);
  }

  @GetMapping("/api/estudiantes/{id}/contacto")
  public ContactoResponse contacto(@PathVariable Long id) {
    return uc.contacto(id);
  }
}
