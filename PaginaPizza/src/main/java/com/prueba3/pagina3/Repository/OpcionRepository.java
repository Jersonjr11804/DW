package com.prueba3.pagina3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prueba3.pagina3.Model.Opcion;
@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {
}