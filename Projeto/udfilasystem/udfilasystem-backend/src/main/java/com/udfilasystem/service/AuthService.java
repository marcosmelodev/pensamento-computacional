package com.udfilasystem.service;

import com.udfilasystem.dto.*;
import com.udfilasystem.entity.RefreshToken;
import com.udfilasystem.entity.Usuario;
import com.udfilasystem.exception.AutenticacaoException;
import com.udfilasystem.exception.RecursoExisteException;
import com.udfilasystem.repository.RefreshTokenRepository;
import com.udfilasystem.repository.UsuarioRepository;
import com.udfilasystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository    usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TotpService          totpService;
    private final JwtService           jwtService;
    private final PasswordEncoder      passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /**
     * Cadastra novo usuario, gera segredo TOTP e retorna QR Code em Base64.
     */
    @Transactional
    public CadastroResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RecursoExisteException("E-mail ja cadastrado no sistema");
        }

        String senhaHash   = passwordEncoder.encode(request.senha());
        String totpSecret  = totpService.gerarSegredo();

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(senhaHash)
                .totpSecret(totpSecret)
                .totpAtivo(false)
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        log.info("Novo usuario cadastrado: {}", request.email());

        String qrCodeBase64 = totpService.gerarQrCodeBase64(request.email(), totpSecret);

        return new CadastroResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                qrCodeBase64,
                "Escaneie o QR Code com o Microsoft Authenticator para configurar a autenticacao em 2 fatores."
        );
    }

    /**
     * Primeira etapa: valida email + senha.
     * Retorna indicador de que precisa de 2FA.
     */
    @Transactional(readOnly = true)
    public LoginEtapa1Response autenticarEtapa1(LoginEtapa1Request request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );
        } catch (BadCredentialsException e) {
            throw new AutenticacaoException("Email ou senha incorretos");
        }

        return new LoginEtapa1Response(request.email(), true, "Informe o codigo do Microsoft Authenticator");
    }

    /**
     * Segunda etapa: valida codigo TOTP e emite tokens JWT.
     */
    @Transactional
    public TokenResponse autenticarEtapa2(LoginEtapa2Request request) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(request.email())
                .orElseThrow(() -> new AutenticacaoException("Usuario nao encontrado"));

        boolean codigoValido = totpService.verificarCodigo(usuario.getTotpSecret(), request.codigoTotp());
        if (!codigoValido) {
            log.warn("Codigo TOTP invalido para: {}", request.email());
            throw new AutenticacaoException("Codigo de autenticacao invalido ou expirado");
        }

        if (!usuario.isTotpAtivo()) {
            usuarioRepository.ativarTotp(usuario.getId());
        }

        return emitirTokens(usuario);
    }

    /**
     * Ativa o TOTP apos verificacao na tela de cadastro.
     */
    @Transactional
    public TokenResponse confirmarTotp(ConfirmarTotpRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(request.email())
                .orElseThrow(() -> new AutenticacaoException("Usuario nao encontrado"));

        boolean codigoValido = totpService.verificarCodigo(usuario.getTotpSecret(), request.codigoTotp());
        if (!codigoValido) {
            throw new AutenticacaoException("Codigo TOTP invalido. Verifique o Microsoft Authenticator.");
        }

        usuarioRepository.ativarTotp(usuario.getId());
        log.info("2FA ativado para: {}", usuario.getEmail());

        return emitirTokens(usuario);
    }

    /**
     * Renova o access token usando um refresh token valido.
     */
    @Transactional
    public TokenResponse renovarToken(String refreshToken) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .filter(RefreshToken::isValido)
                .orElseThrow(() -> new AutenticacaoException("Refresh token invalido ou expirado"));

        String novoJwt = jwtService.gerarToken(rt.getUsuario().getEmail(),
                Map.of("role", rt.getUsuario().getRole().name()));

        return new TokenResponse(novoJwt, refreshToken, jwtService.extrairEmail(novoJwt));
    }

    /**
     * Revoga todos os tokens do usuario (logout).
     */
    @Transactional
    public void logout(String email) {
        usuarioRepository.findByEmailAndAtivoTrue(email)
                .ifPresent(u -> refreshTokenRepository.revogarTodosPorUsuario(u.getId()));
        log.info("Logout realizado para: {}", email);
    }

    private TokenResponse emitirTokens(Usuario usuario) {
        String jwt = jwtService.gerarToken(usuario.getEmail(),
                Map.of("role", usuario.getRole().name(), "nome", usuario.getNome()));

        String rawRefresh = UUID.randomUUID().toString();
        RefreshToken rt = RefreshToken.builder()
                .token(rawRefresh)
                .usuario(usuario)
                .expiradoEm(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .build();
        refreshTokenRepository.save(rt);

        return new TokenResponse(jwt, rawRefresh, usuario.getEmail());
    }
}
