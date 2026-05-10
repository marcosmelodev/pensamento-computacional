package com.udfilasystem.dto;

import com.udfilasystem.entity.TipoSetor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilaRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 100)
        String nome,

        String descricao,

        @NotNull(message = "Tipo de setor e obrigatorio")
        TipoSetor tipo,

        @Min(value = 1, message = "Capacidade minima e 1")
        int maxCapacidade
) {}
