package com.udfilasystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "entradas_fila", indexes = {
    @Index(name = "idx_entrada_fila",    columnList = "fila_id, status"),
    @Index(name = "idx_entrada_usuario", columnList = "usuario_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EntradaFila {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fila_id", nullable = false)
    private Fila fila;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "posicao", nullable = false)
    private int posicao;

    /**
     * Código da senha gerado na entrada (ex: C01, F03, S07).
     * Prefixo do setor + posição formatada com dois dígitos.
     */
    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.AGUARDANDO;

    @CreationTimestamp
    @Column(name = "entrou_em", updatable = false)
    private LocalDateTime entrouEm;

    @Column(name = "atendido_em")
    private LocalDateTime atendidoEm;

    public enum Status {
        AGUARDANDO, CHAMADO, ATENDIDO, CANCELADO
    }
}
