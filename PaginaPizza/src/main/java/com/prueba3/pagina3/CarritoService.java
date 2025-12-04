package com.prueba3.pagina3;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {
    
    private final ItemCarritoRepository itemCarritoRepository;
    private final List<ItemCarrito> itemsCarrito = new ArrayList<>();

    @Autowired
    public CarritoService(ItemCarritoRepository itemCarritoRepository) {
        this.itemCarritoRepository = itemCarritoRepository;
    }

    // Agregar item al carrito
    @Transactional
    public void agregarItem(String usuarioEmail, Opcion opcion, String tamano, int cantidad) {
        // Verificar si ya existe un item igual (misma pizza y tamaño)
        ItemCarrito itemExistente = (usuarioEmail != null)
                ? itemCarritoRepository.findByUsuarioEmailAndOpcionIdAndTamano(usuarioEmail, opcion.getId(), tamano).orElse(null)
                : buscarItemExistente(opcion.getId(), tamano);
        
        if (itemExistente != null) {
            // Si existe, aumentar la cantidad
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            if (usuarioEmail != null) {
                itemCarritoRepository.save(itemExistente);
            }
        } else {
            // Si no existe, crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito(opcion, tamano, cantidad);
            if (usuarioEmail != null) {
                nuevoItem.setUsuarioEmail(usuarioEmail);
                itemCarritoRepository.save(nuevoItem);
            } else {
                itemsCarrito.add(nuevoItem);
            }
        }
    }

    // Buscar item existente por pizza y tamaño
    private ItemCarrito buscarItemExistente(Long opcionId, String tamano) {
        return itemsCarrito.stream()
                .filter(item -> item.getOpcion().getId().equals(opcionId) && 
                               item.getTamano().equalsIgnoreCase(tamano))
                .findFirst()
                .orElse(null);
    }

    // Obtener todos los items del carrito
    public List<ItemCarrito> obtenerItems(String usuarioEmail) {
        if (usuarioEmail != null) {
            return itemCarritoRepository.findByUsuarioEmail(usuarioEmail);
        }
        return new ArrayList<>(itemsCarrito);
    }

    // Eliminar item por ID
    @Transactional
    public boolean eliminarItem(Long itemId, String usuarioEmail) {
        if (usuarioEmail != null) {
            if (itemCarritoRepository.existsById(itemId)) {
                itemCarritoRepository.deleteById(itemId);
                return true;
            }
            return false;
        }
        return itemsCarrito.removeIf(item -> item.getId().equals(itemId));
    }

    // Vaciar carrito completo
    @Transactional
    public void vaciarCarrito(String usuarioEmail) {
        if (usuarioEmail != null) {
            itemCarritoRepository.deleteByUsuarioEmail(usuarioEmail);
        } else {
            itemsCarrito.clear();
        }
    }

    // Calcular total del carrito
    public double calcularTotal(String usuarioEmail) {
        return obtenerItems(usuarioEmail).stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    // Obtener cantidad total de items
    public int obtenerCantidadItems(String usuarioEmail) {
        return obtenerItems(usuarioEmail).stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    // Verificar si el carrito está vacío
    public boolean estaVacio(String usuarioEmail) {
        return obtenerItems(usuarioEmail).isEmpty();
    }

    // Obtener item por ID
    public ItemCarrito obtenerItemPorId(Long itemId, String usuarioEmail) {
        if (usuarioEmail != null) {
            return itemCarritoRepository.findById(itemId).orElse(null);
        }
        return itemsCarrito.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    // Actualizar cantidad de un item específico
    @Transactional
    public boolean actualizarCantidad(Long itemId, int nuevaCantidad, String usuarioEmail) {
        ItemCarrito item = obtenerItemPorId(itemId, usuarioEmail);
        if (item != null && nuevaCantidad > 0) {
            item.setCantidad(nuevaCantidad);
            if (usuarioEmail != null) {
                itemCarritoRepository.save(item);
            }
            return true;
        } else if (item != null && nuevaCantidad <= 0) {
            // Si la cantidad es 0 o negativa, eliminar el item
            return eliminarItem(itemId, usuarioEmail);
        }
        return false;
    }

    // Migrar carrito en memoria (invitado) al carrito persistido del usuario
    @Transactional
    public void migrarCarritoInvitado(String usuarioEmail) {
        if (usuarioEmail == null || itemsCarrito.isEmpty()) {
            return;
        }

        // Persistir cada item del carrito de invitado al carrito del usuario
        for (ItemCarrito item : new ArrayList<>(itemsCarrito)) {
            item.setUsuarioEmail(usuarioEmail);
            itemCarritoRepository.save(item);
        }
        // Limpiar carrito de invitado
        itemsCarrito.clear();
    }
}