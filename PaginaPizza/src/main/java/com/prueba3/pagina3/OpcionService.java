package com.prueba3.pagina3;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Service
public class OpcionService {

    private final OpcionRepository opcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
    }

    public Map<String, String> validarLogin(String email, String password) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            // Soporta contraseñas ya encriptadas y, como compatibilidad temporal,
            // contraseñas antiguas guardadas en texto plano.
            if (passwordEncoder.matches(password, u.getPassword()) || password.equals(u.getPassword())) {
                Map<String, String> info = new HashMap<>();
                info.put("nombre", u.getNombre());
                info.put("email", u.getEmail());
                return info;
            }
        }
        return null;
    }

    public List<Map<String, String>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Map<String, String>> resultado = new ArrayList<>();
        for (Usuario u : usuarios) {
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