package com.prueba3.pagina3.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prueba3.pagina3.Model.Opcion;
import com.prueba3.pagina3.Service.CarritoService;
import com.prueba3.pagina3.Service.OpcionService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OpcionController {

    private final OpcionService opcionService;
    private final CarritoService carritoService;

    public OpcionController(OpcionService opcionService, CarritoService carritoService) {
        this.opcionService = opcionService;
        this.carritoService = carritoService;
    }

    @GetMapping("/") // URL final: /opciones/inicio
    public String mostrarOpciones(Model model, HttpSession session) {
        model.addAttribute("opciones", opcionService.listarOpciones());

        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadItems(usuarioEmail));

        return "seleccion"; // busca seleccion.html en templates
    }


    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long opcionId, @RequestParam String tamano, @RequestParam int cantidad, RedirectAttributes redirectAttributes, HttpSession session) {
        Opcion opcion = opcionService.obtenerPorId(opcionId);
        if (opcion != null && cantidad > 0) {
            String usuarioEmail = (String) session.getAttribute("usuarioEmail");
            carritoService.agregarItem(usuarioEmail, opcion, tamano, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Pizza agregada al carrito exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al agregar la pizza al carrito");
        }
        
        return "redirect:/";
    }
    
    @GetMapping("/carrito")
    public String mostrarCarrito(Model model, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        model.addAttribute("itemsCarrito", carritoService.obtenerItems(usuarioEmail));
        model.addAttribute("totalCarrito", carritoService.calcularTotal(usuarioEmail));
        model.addAttribute("cantidadItems", carritoService.obtenerCantidadItems(usuarioEmail));
        model.addAttribute("carritoVacio", carritoService.estaVacio(usuarioEmail));
        return "carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam Long itemId, RedirectAttributes redirectAttributes, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (carritoService.eliminarItem(itemId, usuarioEmail)) {
            redirectAttributes.addFlashAttribute("mensaje", "Item eliminado del carrito");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el item");
        }
        
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(RedirectAttributes redirectAttributes, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        carritoService.vaciarCarrito(usuarioEmail);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado exitosamente");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCantidadCarrito(@RequestParam Long itemId, @RequestParam int cantidad, RedirectAttributes redirectAttributes, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (carritoService.actualizarCantidad(itemId, cantidad, usuarioEmail)) {
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cantidad");
        }
        return "redirect:/carrito";
    }
    
    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        // Verificar si el email ya existe
        if (opcionService.emailExiste(email)) {
            model.addAttribute("error", "Este email ya está registrado");
            return "registro";
        }
        
        // Persistir usuario en la base de datos
        opcionService.registrarUsuario(nombre, apellidos, telefono, direccion, email, password);
        
        // Guardar en sesión
        session.setAttribute("usuarioEmail", email);
        session.setAttribute("usuarioNombre", nombre);
        
        // Si había pizza seleccionada, ir al carrito
        Long pizzaId = (Long) session.getAttribute("pizzaSeleccionada");
        if (pizzaId != null) {
            return "redirect:/carrito";
        }
        
        model.addAttribute("mensaje", "¡Registro exitoso! Usuario agregado a la lista.");
        return "registro";
    }

    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (email.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Por favor completa todos los campos");
            return "login";
        }
        
        // BUSCAR credenciales en la lista de OpcionRepository
        java.util.Map<String, String> usuario = opcionService.validarLogin(email, password);
        
        if (usuario == null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }
        
        // Login exitoso
        session.setAttribute("usuarioEmail", email);
        session.setAttribute("usuarioNombre", usuario.get("nombre"));

        // Migrar carrito de invitado al carrito del usuario
        carritoService.migrarCarritoInvitado(email);
        
        // Verificar si hay una redirección pendiente (usando sesión)
        String redirectAfterLogin = (String) session.getAttribute("redirectAfterLogin");
        if (redirectAfterLogin != null) {
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectAfterLogin;
        }
        
        // Si había pizza seleccionada, ir al carrito
        Long pizzaId = (Long) session.getAttribute("pizzaSeleccionada");
        if (pizzaId != null) {
            return "redirect:/carrito";
        }
        
        // Por defecto, redirigir a la página principal
        redirectAttributes.addFlashAttribute("mensaje", "¡Bienvenido " + usuario.get("nombre") + "!");
        return "redirect:/";
    }

    @PostMapping("/confirmar")
    public String confirmarPedido(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para confirmar tu pedido");
            session.setAttribute("redirectAfterLogin", "/pago");
            return "redirect:/login";
        }
        if (carritoService.estaVacio(usuarioEmail)) {
            redirectAttributes.addFlashAttribute("error", "No hay items en el carrito para confirmar");
            return "redirect:/carrito";
        }
        return "redirect:/pago";
    }

    @GetMapping("/pago")
    public String mostrarPago(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para continuar con el pago");
            session.setAttribute("redirectAfterLogin", "/pago");
            return "redirect:/login";
        }
        if (carritoService.estaVacio(usuarioEmail)) {
            redirectAttributes.addFlashAttribute("error", "Tu carrito está vacío");
            return "redirect:/carrito";
        }

        model.addAttribute("itemsCarrito", carritoService.obtenerItems(usuarioEmail));
        model.addAttribute("totalCarrito", carritoService.calcularTotal(usuarioEmail));
        model.addAttribute("usuarioEmail", usuarioEmail);
        return "pago";
    }

    @PostMapping("/pago")
    public String procesarPago(
            @RequestParam("metodo") String metodo,
            @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
            @RequestParam(value = "nombreTarjeta", required = false) String nombreTarjeta,
            @RequestParam(value = "fechaTarjeta", required = false) String fechaTarjeta,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para confirmar el pago");
            session.setAttribute("redirectAfterLogin", "/pago");
            return "redirect:/login";
        }
        if (carritoService.estaVacio(usuarioEmail)) {
            redirectAttributes.addFlashAttribute("error", "No hay items en el carrito para confirmar");
            return "redirect:/carrito";
        }

        // Preparar datos para la vista de resultado
        model.addAttribute("itemsPedido", carritoService.obtenerItems(usuarioEmail));
        model.addAttribute("totalPedido", carritoService.calcularTotal(usuarioEmail));
        model.addAttribute("cantidadItems", carritoService.obtenerCantidadItems(usuarioEmail));
        model.addAttribute("usuarioEmail", usuarioEmail);
        model.addAttribute("metodoPago", metodo);

        if (metodo != null && metodo.equalsIgnoreCase("Tarjeta") && numeroTarjeta != null && numeroTarjeta.length() >= 4) {
            String last4 = numeroTarjeta.substring(numeroTarjeta.length() - 4);
            model.addAttribute("detalleTarjeta", "Tarjeta **** **** **** " + last4);
        }

        // Vaciar carrito después de confirmar
        carritoService.vaciarCarrito(usuarioEmail);

        return "resultado";
    }
    
    
    @GetMapping("/usuarios")
    public String verUsuarios(Model model) {
        java.util.List<java.util.Map<String, String>> usuarios = opcionService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("total", usuarios.size());
        return "usuarios";
    }
    
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada exitosamente");
        return "redirect:/";
    }
}