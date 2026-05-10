package com.udfilasystem.repository;

import com.udfilasystem.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revogado = true WHERE rt.usuario.id = :usuarioId")
    void revogarTodosPorUsuario(Long usuarioId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiradoEm < CURRENT_TIMESTAMP OR rt.revogado = true")
    int limparTokensExpirados();
}
