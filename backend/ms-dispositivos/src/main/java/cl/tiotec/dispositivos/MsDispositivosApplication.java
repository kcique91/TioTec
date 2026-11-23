package cl.tiotec.dispositivos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada: levanta el servidor en el puerto 8082
@SpringBootApplication
public class MsDispositivosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDispositivosApplication.class, args);
    }
}
