package cl.tiotec.dispositivos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

// Entidad: cada objeto es una fila de la tabla "dispositivos"
@Entity
@Table(name = "dispositivos")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Por ejemplo "Android 14"
    @NotBlank(message = "El sistema es obligatorio")
    private String sistema;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSistema() { return sistema; }
    public void setSistema(String sistema) { this.sistema = sistema; }
}
