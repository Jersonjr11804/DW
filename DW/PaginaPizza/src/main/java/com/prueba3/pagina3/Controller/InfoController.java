package com.prueba3.pagina3.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    /**
     * Obtiene la página de misión y visión de la empresa
     * 
     * @return Vista de misión y visión
     */
    @GetMapping("/misionvision")
    public String misionVision() {
        return "misionvision"; // busca misionvision.html en templates
    }
}

