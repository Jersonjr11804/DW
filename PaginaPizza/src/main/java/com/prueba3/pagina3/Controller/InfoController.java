package com.prueba3.pagina3.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Info", description = "Endpoints de información")
public class InfoController {

    @GetMapping("/misionvision")
    @Operation(summary = "Mostrar misión y visión", description = "Retorna la vista misionvision.html")
    public String misionVision() {
        return "misionvision"; // busca misionvision.html en templates
    }
}

