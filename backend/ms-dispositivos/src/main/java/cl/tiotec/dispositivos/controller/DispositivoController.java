package cl.tiotec.dispositivos.controller;

import cl.tiotec.dispositivos.model.Dispositivo;
import cl.tiotec.dispositivos.service.DispositivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints REST que consume la app movil
@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final DispositivoService service;

    public DispositivoController(DispositivoService service) {
        this.service = service;
    }

    // GET /api/dispositivos -> lista todos
    @GetMapping
    public List<Dispositivo> listar() {
        return service.listar();
    }

    // GET /api/dispositivos/{id} -> uno solo (404 si no existe)
    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/dispositivos -> crea (201)
    @PostMapping
    public ResponseEntity<Dispositivo> crear(@Valid @RequestBody Dispositivo dispositivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dispositivo));
    }

    // PUT /api/dispositivos/{id} -> actualiza (404 si no existe)
    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> actualizar(@PathVariable Long id,
                                                  @Valid @RequestBody Dispositivo dispositivo) {
        return service.actualizar(id, dispositivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/dispositivos/{id} -> elimina (204, o 404 si no existe)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
