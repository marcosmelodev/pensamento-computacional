package com.udfilasystem.dto;

/**
 * Status em tempo real de uma fila por setor.
 * Retornado pelo endpoint GET /api/filas/status.
 */
public record FilaStatusDto(
        Long   filaId,
        String tipo,
        String descricao,
        String prefixo,
        long   aguardando,
        String atualAtendendo,
        int    tempoMedioMinutos
) {}
