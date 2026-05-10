package com.udfilasystem.repository;

import com.udfilasystem.entity.Fila;
import com.udfilasystem.entity.TipoSetor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilaRepository extends JpaRepository<Fila, Long> {

    Page<Fila> findByAtivaTrue(Pageable pageable);

    /** Busca a fila ativa de um setor específico (máx. 1 por tipo). */
    Optional<Fila> findByTipoAndAtivaTrue(TipoSetor tipo);

    @Query("SELECT COUNT(e) FROM EntradaFila e WHERE e.fila.id = :filaId AND e.status = 'AGUARDANDO'")
    long contarAguardando(Long filaId);
}
