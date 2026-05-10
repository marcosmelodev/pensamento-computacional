package com.udfilasystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginEtapa2Request(
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        String email,

        @NotNull(message = "Codigo TOTP e obrigatorio")
        Integer codigoTotp
) {}
