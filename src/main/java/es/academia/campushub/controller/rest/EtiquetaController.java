package es.academia.campushub.controller.rest;

import es.academia.campushub.controller.dto.ActividadResponse;
import es.academia.campushub.controller.dto.CreateEtiquetaRequest;
import es.academia.campushub.controller.dto.EtiquetaResponse;
import es.academia.campushub.service.CampusHubService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de etiquetas. */
@RestController
public class EtiquetaController {
  private final CampusHubService service;

  public EtiquetaController(CampusHubService service) {
    this.service = service;
  }

  @PostMapping("/api/etiquetas")
  public ResponseEntity<EtiquetaResponse> crear(@RequestBody CreateEtiquetaRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearEtiqueta(r));
  }

  @PutMapping("/api/actividades/{id}/etiquetas/{etiquetaId}")
  public ResponseEntity<Void> asociar(@PathVariable Long id, @PathVariable Long etiquetaId) {
    service.asociarEtiqueta(id, etiquetaId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/api/etiquetas/{id}/actividades")
  public List<ActividadResponse> actividades(@PathVariable Long id) {
    return service.actividadesPorEtiqueta(id);
  }
}
