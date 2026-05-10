package com.udfilasystem.dto;

public record CadastroResponse(
        Long id,
        String nome,
        String email,
        String qrCodeBase64,
        String mensagem
) {}
