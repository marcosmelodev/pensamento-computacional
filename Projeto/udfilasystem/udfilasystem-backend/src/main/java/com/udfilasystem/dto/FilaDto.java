package com.udfilasystem.dto;

public record FilaDto(
        Long   id,
        String nome,
        String descricao,
        String tipo,
        String prefixo,
        int    maxCapacidade,
        long   qtdAguardando
) {}
