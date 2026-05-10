package com.udfilasystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmarTotpRequest(
        @NotBlank @Email
        String email,

        @NotNull
        Integer codigoTotp
) {}
