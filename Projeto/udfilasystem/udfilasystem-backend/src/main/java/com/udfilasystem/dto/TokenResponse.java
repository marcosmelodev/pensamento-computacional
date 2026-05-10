package com.udfilasystem.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String email
) {}
