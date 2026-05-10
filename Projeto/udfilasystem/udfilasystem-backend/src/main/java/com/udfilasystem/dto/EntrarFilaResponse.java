package com.udfilasystem.dto;

import java.time.LocalDateTime;

public record EntrarFilaResponse(
        Long          entradaId,
        Long          filaId,
        String        nomeFila,
        String        tipo,
        String        codigo,
        int           posicao,
        int           pessoasNaFrente,
        int           previsaoMinutos,
        LocalDateTime entrouEm
) {}
