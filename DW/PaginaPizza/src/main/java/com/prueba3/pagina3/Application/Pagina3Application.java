package com.prueba3.pagina3.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication   // ⬅ ESTO ES LO QUE FALTABA
public class Pagina3Application {

    public static void main(String[] args) {
        SpringApplication.run(Pagina3Application.class, args);
        System.out.println("Aplicación iniciada correctamente.");
    }

}
