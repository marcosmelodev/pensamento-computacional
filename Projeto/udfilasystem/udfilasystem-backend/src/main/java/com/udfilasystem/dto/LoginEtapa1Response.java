package com.udfilasystem.dto;

public record LoginEtapa1Response(
        String email,
        boolean requer2FA,
        String mensagem
) {}
