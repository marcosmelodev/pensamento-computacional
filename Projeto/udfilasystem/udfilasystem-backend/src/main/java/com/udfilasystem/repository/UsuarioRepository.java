package com.udfilasystem.repository;

import com.udfilasystem.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.totpAtivo = true WHERE u.id = :id")
    void ativarTotp(Long id);
}
