package cl.tiotec.solicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada: levanta el servidor en el puerto 8081
@SpringBootApplication
public class MsSolicitudesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSolicitudesApplication.class, args);
    }
}