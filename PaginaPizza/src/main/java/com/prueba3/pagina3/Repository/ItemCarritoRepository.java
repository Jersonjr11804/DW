package com.prueba3.pagina3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prueba3.pagina3.Model.ItemCarrito;

import java.util.List;
import java.util.Optional;
@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    @Query("SELECT i FROM ItemCarrito i JOIN FETCH i.opcion WHERE i.usuarioEmail = :usuarioEmail")
    List<ItemCarrito> findByUsuarioEmail(@Param("usuarioEmail") String usuarioEmail);
    
    Optional<ItemCarrito> findByUsuarioEmailAndOpcionIdAndTamano(String usuarioEmail, Long opcionId, String tamano);
    void deleteByUsuarioEmail(String usuarioEmail);
}