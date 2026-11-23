package cl.tiotec.solicitudes.service;

import cl.tiotec.solicitudes.model.Solicitud;
import cl.tiotec.solicitudes.repository.SolicitudRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Logica de negocio: el controlador le pide las cosas a esta clase
@Service
public class SolicitudService {

    private final SolicitudRepository repository;

    public SolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    // Todas, de la mas nueva a la mas antigua
    public List<Solicitud> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Solicitud> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Toda solicitud nueva parte en "Pendiente" con la fecha actual
    public Solicitud crear(Solicitud solicitud) {
        solicitud.setId(null);
        if (solicitud.getEstado() == null || solicitud.getEstado().isBlank()) {
            solicitud.setEstado("Pendiente");
        }
        if (solicitud.getFecha() == null) {
            solicitud.setFecha(System.currentTimeMillis());
        }
        return repository.save(solicitud);
    }

    // Actualiza categoria, descripcion y estado (la fecha no cambia)
    public Optional<Solicitud> actualizar(Long id, Solicitud datos) {
        return repository.findById(id).map(actual -> {
            actual.setCategoria(datos.getCategoria());
            actual.setDescripcion(datos.getDescripcion());
            actual.setEstado(datos.getEstado());
            return repository.save(actual);
        });
    }

    // Devuelve false si no existia
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}