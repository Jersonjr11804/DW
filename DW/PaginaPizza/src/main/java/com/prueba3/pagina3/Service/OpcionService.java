package com.prueba3.pagina3.Service;

import org.springframework.stereotype.Service;

import com.prueba3.pagina3.Model.Opcion;
import com.prueba3.pagina3.Model.Usermodel;
import com.prueba3.pagina3.Repository.OpcionRepository;
import com.prueba3.pagina3.Repository.UsuarioRepository;
import java.util.*;

@Service
public class OpcionService {

    private final OpcionRepository opcionRepository;
    private final UsuarioRepository usuarioRepository;

    public OpcionService(OpcionRepository opcionRepository, UsuarioRepository usuarioRepository) {
        this.opcionRepository = opcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Métodos de pizzas
    public List<Opcion> listarOpciones() {
        return opcionRepository.findAll();
    }

    public Opcion obtenerPorId(Long id) {
        return opcionRepository.findById(id).orElse(null);
    }

    // Métodos de usuarios
    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public void registrarUsuario(String nombre, String apellidos, String telefono, String direccion, String email, String password) {
        Usermodel usuario = new Usermodel();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setEmail(email);
        
        usuarioRepository.save(usuario);
    }

    public Map<String, String> validarLogin(String email, String password) {
        Optional<Usermodel> opt = usuarioRepository.findByEmail(email);
        if (opt.isPresent()) {
            Usermodel u = opt.get();

            
        }
        return null;
    }

    public List<Map<String, String>> obtenerTodosLosUsuarios() {
        List<Usermodel> usuarios = usuarioRepository.findAll();
        List<Map<String, String>> resultado = new ArrayList<>();
        for (Usermodel u : usuarios) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("nombre", u.getNombre());
            m.put("apellidos", u.getApellidos());
            m.put("telefono", u.getTelefono());
            m.put("direccion", u.getDireccion());
            m.put("email", u.getEmail());
            resultado.add(m);
        }
        return resultado;
    }
}