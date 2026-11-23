package cl.tiotec.solicitudes.repository;

import cl.tiotec.solicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository ya trae findAll, findById, save, deleteById, etc.
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
}