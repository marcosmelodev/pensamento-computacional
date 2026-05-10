package com.udfilasystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token",    columnList = "token"),
    @Index(name = "idx_refresh_usuario",  columnList = "usuario_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "expirado_em", nullable = false)
    private LocalDateTime expiradoEm;

    @Column(name = "revogado", nullable = false)
    @Builder.Default
    private boolean revogado = false;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(expiradoEm);
    }

    public boolean isValido() {
        return !revogado && !isExpirado();
    }
}
