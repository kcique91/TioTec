package cl.tiotec.dispositivos.service;

import cl.tiotec.dispositivos.model.Dispositivo;
import cl.tiotec.dispositivos.repository.DispositivoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Logica de negocio: el controlador le pide las cosas a esta clase
@Service
public class DispositivoService {

    private final DispositivoRepository repository;

    public DispositivoService(DispositivoRepository repository) {
        this.repository = repository;
    }

    // Todos, del mas nuevo al mas antiguo
    public List<Dispositivo> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Dispositivo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Dispositivo crear(Dispositivo dispositivo) {
        dispositivo.setId(null);
        return repository.save(dispositivo);
    }

    // Actualiza nombre y sistema
    public Optional<Dispositivo> actualizar(Long id, Dispositivo datos) {
        return repository.findById(id).map(actual -> {
            actual.setNombre(datos.getNombre());
            actual.setSistema(datos.getSistema());
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
