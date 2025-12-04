package com.prueba3.pagina3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prueba3.pagina3.Model.ItemCarrito;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    List<ItemCarrito> findByUsuarioEmail(String usuarioEmail);
    Optional<ItemCarrito> findByUsuarioEmailAndOpcionIdAndTamano(String usuarioEmail, Long opcionId, String tamano);
    void deleteByUsuarioEmail(String usuarioEmail);
}