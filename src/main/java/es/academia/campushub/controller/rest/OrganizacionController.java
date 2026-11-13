package es.academia.campushub.controller.rest;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.service.CampusHubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints REST de grupos, departamentos y docentes. */
@RestController
public class OrganizacionController {
  private final CampusHubService service;

  public OrganizacionController(CampusHubService service) {
    this.service = service;
  }

  @PostMapping("/api/grupos")
  public ResponseEntity<GrupoResponse> grupo(@RequestBody CreateGrupoRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearGrupo(r));
  }

  @PostMapping("/api/departamentos")
  public ResponseEntity<DepartamentoResponse> departamento(
      @RequestBody CreateDepartamentoRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDepartamento(r));
  }

  @PostMapping("/api/docentes")
  public ResponseEntity<DocenteResponse> docente(@RequestBody CreateDocenteRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDocente(r));
  }
}
