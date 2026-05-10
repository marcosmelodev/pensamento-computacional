package com.udfilasystem.controller;

import com.udfilasystem.dto.*;
import com.udfilasystem.exception.RegraDeNegocioException;
import com.udfilasystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/cadastrar
     * Registra novo usuario e retorna QR Code para configurar o 2FA.
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<CadastroResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        if (!request.senhasConferem()) {
            throw new RegraDeNegocioException("As senhas nao conferem");
        }
        CadastroResponse response = authService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login/etapa1
     * Valida email e senha. Retorna indicador de que 2FA e necessario.
     */
    @PostMapping("/login/etapa1")
    public ResponseEntity<LoginEtapa1Response> loginEtapa1(@Valid @RequestBody LoginEtapa1Request request) {
        return ResponseEntity.ok(authService.autenticarEtapa1(request));
    }

    /**
     * POST /api/auth/login/etapa2
     * Valida codigo TOTP e retorna tokens JWT de acesso e refresh.
     */
    @PostMapping("/login/etapa2")
    public ResponseEntity<TokenResponse> loginEtapa2(@Valid @RequestBody LoginEtapa2Request request) {
        return ResponseEntity.ok(authService.autenticarEtapa2(request));
    }

    /**
     * POST /api/auth/totp/confirmar
     * Ativa o 2FA apos o cadastro e retorna tokens JWT.
     */
    @PostMapping("/totp/confirmar")
    public ResponseEntity<TokenResponse> confirmarTotp(@Valid @RequestBody ConfirmarTotpRequest request) {
        return ResponseEntity.ok(authService.confirmarTotp(request));
    }

    /**
     * POST /api/auth/token/renovar
     * Renova o access token usando o refresh token.
     */
    @PostMapping("/token/renovar")
    public ResponseEntity<TokenResponse> renovarToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.renovarToken(refreshToken));
    }

    /**
     * POST /api/auth/logout
     * Revoga todos os refresh tokens do usuario autenticado.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
