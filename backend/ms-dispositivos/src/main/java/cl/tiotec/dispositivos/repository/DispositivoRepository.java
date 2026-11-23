package cl.tiotec.dispositivos.repository;

import cl.tiotec.dispositivos.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository ya trae findAll, findById, save, deleteById, etc.
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
}
