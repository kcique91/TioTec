package cl.tiotec.solicitudes.controller;

import cl.tiotec.solicitudes.model.Solicitud;
import cl.tiotec.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints REST que consume la app movil
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    // GET /api/solicitudes -> lista todas
    @GetMapping
    public List<Solicitud> listar() {
        return service.listar();
    }

    // GET /api/solicitudes/{id} -> una sola (404 si no existe)
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/solicitudes -> crea (201)
    @PostMapping
    public ResponseEntity<Solicitud> crear(@Valid @RequestBody Solicitud solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(solicitud));
    }

    // PUT /api/solicitudes/{id} -> actualiza (404 si no existe)
    @PutMapping("/{id}")
    public ResponseEntity<Solicitud> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody Solicitud solicitud) {
        return service.actualizar(id, solicitud)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/solicitudes/{id} -> elimina (204, o 404 si no existe)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}