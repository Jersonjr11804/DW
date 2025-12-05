package com.prueba3.pagina3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prueba3.pagina3.Model.Usermodel;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usermodel, Long> {
    Optional<Usermodel> findByEmail(String email);
    boolean existsByEmail(String email);
}