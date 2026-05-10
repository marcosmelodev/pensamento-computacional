package com.udfilasystem.repository;

import com.udfilasystem.entity.EntradaFila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntradaFilaRepository extends JpaRepository<EntradaFila, Long> {

    List<EntradaFila> findByFilaIdAndStatusOrderByPosicaoAsc(Long filaId, EntradaFila.Status status);

    Optional<EntradaFila> findByFilaIdAndUsuarioIdAndStatus(Long filaId, Long usuarioId, EntradaFila.Status status);

    /** Retorna a entrada com status CHAMADO de menor posição (senha sendo atendida agora). */
    Optional<EntradaFila> findTopByFilaIdAndStatusOrderByPosicaoAsc(Long filaId, EntradaFila.Status status);

    @Query("SELECT COALESCE(MAX(e.posicao), 0) FROM EntradaFila e WHERE e.fila.id = :filaId AND e.status IN ('AGUARDANDO','CHAMADO')")
    int obterMaximaPosicao(Long filaId);
}
